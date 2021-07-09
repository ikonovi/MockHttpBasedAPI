package ik;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import ik.random.RandomGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

public class Scenario01Test {

    private static final Logger log = LogManager.getLogger(Scenario01Test.class);

    @Rule
    public WireMockRule mockRule = new WireMockRule(wireMockConfig().port(8080));


    @Test
    public void testHttpService() {
//        int port = wireMockRule.port();
//        log.debug("port={}", port);

        RandomGenerator randomGenerator = new RandomGenerator();
        String randomString1 = randomGenerator.randomAlphanumeric(100);
        String randomString2 = randomGenerator.randomAlphanumeric(20);

        // #1
        mockRule.stubFor(
            get("/plaintext/mapping1")
                .willReturn(aResponse()
                    .withBody(randomString1)
                    .withHeader("Content-Type", "text/plain")
            )
        );

        // #2
        mockRule.stubFor(
            get(urlPathMatching("/jsontext/mapping2*"))
                    .withQueryParam("testqueryparam", equalTo("*"))
            .willReturn(aResponse()
                    .withBody("{ a: \"a\", b: \"b\" }")
                    .withHeader("Content-Type", "application/json")
            )
        );

        // #3 order makes sense
        mockRule.stubFor(
                post("/jsontext/mapping3")
                        .atPriority(10)
                        .withHeader("CustomType", equalTo("CustomValue"))
                        .withRequestBody(containing("TestValue1"))
                        .willReturn(serverError()
                                .withStatus(500)
                                .withBody(randomString2)
                                .withHeader("Content-Type", "text/plain")
                        )
        );
        mockRule.stubFor(
                post("/jsontext/mapping3")
                        .atPriority(20)
                        .willReturn(
                                notFound()
                        )
        );

        // #4
        int delaySeconds = 10;
        mockRule.stubFor(
            put(urlPathMatching("/*"))
            .willReturn(permanentRedirect("/plaintext/mapping1")
                    .withFixedDelay((int) TimeUnit.SECONDS.toMillis(delaySeconds))
            )
        );

        // for test purpose
        try {
            TimeUnit.SECONDS.sleep(30);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
