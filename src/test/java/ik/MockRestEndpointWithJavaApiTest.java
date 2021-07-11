package ik;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.github.tomakehurst.wiremock.stubbing.ServeEvent;
import ik.random.RandomGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static io.restassured.RestAssured.given;

public class MockRestEndpointWithJavaApiTest {

    private static final Logger log = LogManager.getLogger(MockRestEndpointWithJavaApiTest.class);
    String mock2RequestBody = "{ a: \"a\", b: \"b\" }";
    RandomGenerator randomGenerator = new RandomGenerator();

    @Rule
    public WireMockRule mockRule = new WireMockRule(wireMockConfig().port(8080));
//        int port = wireMockRule.port();
//        log.debug("port={}", port);

    @Before
    public void setupMock1() {
        // Mock #1
        String randomString = randomGenerator.randomAlphanumeric(100);
        mockRule.stubFor(
            get("/plaintext/mapping1")
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withBody(randomString)
                            .withHeader("Content-Type", "text/plain")
                    )
        );
    }

    @Before
    public void setupMock2() {
        // Mock #2
        mockRule.stubFor(
            get(urlPathMatching("/jsontext/mapping2*"))
                    .withQueryParam("testqueryparam", equalTo("*"))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withBody(mock2RequestBody)
                            .withHeader("Content-Type", "application/json")
                    )
        );
    }

    @Before
    public void setupMock3() {
        String randomString = randomGenerator.randomAlphanumeric(20);
        mockRule.stubFor(
            post("/jsontext/mapping3")
                .atPriority(10)
                .withHeader("CustomType", equalTo("CustomValue"))
                .withRequestBody(containing("TestValue1"))
                .willReturn(serverError()
                    .withStatus(500)
                    .withBody(randomString)
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
    }

    @Before
    public void setupMock4() {
        int delaySeconds = 10;
        mockRule.stubFor(
            put(urlPathMatching("/*"))
                .willReturn(permanentRedirect("/plaintext/mapping1")
                        .withFixedDelay((int) TimeUnit.SECONDS.toMillis(delaySeconds))
                )
        );

    }

    @Test
    public void testMock2() {
        given()
                .queryParam("testqueryparam", "*")
        .when()
                .get("/jsontext/mapping2")
        .then()
                .statusCode(200)
                .header("Content-Type", "application/json")
                .body(CoreMatchers.equalTo(mock2RequestBody))
                //.log().all(true)
                ;

        // Pause
//        try {
//            TimeUnit.SECONDS.sleep(30);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }

    @After
    public void tearDownPrintEvents() {
        List<ServeEvent> allServeEvents = getAllServeEvents();
        for (ServeEvent event : allServeEvents) {
            log.debug("\nRequest {}\n\nResponse {}", event.getRequest(), event.getResponseDefinition());
        }
    }
}
