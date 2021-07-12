package ik;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.stubbing.ServeEvent;
import ik.util.random.RandomGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hamcrest.CoreMatchers;
import org.testng.annotations.*;
import org.testng.annotations.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;

public class MockRestEndpointWithJavaApiTest {

    private static final Logger log = LogManager.getLogger(MockRestEndpointWithJavaApiTest.class);
    String mock2RequestBody = "{ a: \"a\", b: \"b\" }";
    RandomGenerator randomGenerator = new RandomGenerator();
    String randomString1 = randomGenerator.randomAlphanumeric(100);
    WireMockServer mock;

    @BeforeClass
    public void setupWireMockServer() {
        WireMockConfiguration wireMockConfiguration = new WireMockConfiguration();
        mock = new WireMockServer(wireMockConfiguration);
        mock.start();
    }

    @BeforeMethod
    public void setupMock1() {
        // Mock #1
        mock.stubFor(
            get("/plaintext/mapping1")
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withBody(randomString1)
                            .withHeader("Content-Type", "text/plain")
                            .withHeader("MyHeader", "myHeader")
                    )
        );
    }

    @BeforeMethod
    public void setupMock2() {
        // Mock #2
        mock.stubFor(
            get(urlPathMatching("/jsontext/mapping2*"))
                    .withQueryParam("testqueryparam", equalTo("*"))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withBody(mock2RequestBody)
                            .withHeader("Content-Type", "application/json")
                    )
        );
    }

    @BeforeMethod
    public void setupMock3() {
        String randomString = randomGenerator.randomAlphanumeric(20);
        mock.stubFor(
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
        mock.stubFor(
            post("/jsontext/mapping3")
                .atPriority(20)
                .willReturn(
                        notFound()
                )
        );
    }

    @BeforeMethod
    public void setupMock4() {
        int delaySeconds = 10;
        mock.stubFor(
            put(urlPathMatching("/*"))
                .willReturn(permanentRedirect("/plaintext/mapping1")
                        .withFixedDelay((int) TimeUnit.SECONDS.toMillis(delaySeconds))
                )
        );

    }

    @Test
    public void testEndPoint1() {
        when()
            .get("/plaintext/mapping1")
        .then()
            .statusCode(200)
            .body(CoreMatchers.equalTo(randomString1))
            .header("Content-Type", "text/plain");
    }

    @Test
    public void testEndPoint2() {
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
/*        try {
            TimeUnit.SECONDS.sleep(30);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
    }

    @Test
    public void testEndPoint3() {
        // TODO: ...
    }

    @AfterMethod
    public void tearDownPrintEvents() {
        List<ServeEvent> allServeEvents = getAllServeEvents();
        for (ServeEvent event : allServeEvents) {
            log.debug("\nRequest {}\n\nResponse {}", event.getRequest(), event.getResponseDefinition());
        }
    }

    @AfterClass
    public void tearDownWireMockServer() {
        mock.stop();
    }
}
