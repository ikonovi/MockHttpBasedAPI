package ik;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.stubbing.ServeEvent;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import ik.util.random.RandomGenerator;
import io.restassured.RestAssured;
import lombok.extern.log4j.Log4j2;
import org.hamcrest.CoreMatchers;
import org.testng.annotations.Test;
import org.testng.annotations.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.restassured.RestAssured.*;
import static io.restassured.config.RedirectConfig.redirectConfig;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

@Log4j2
public class MockRestEndpointWithJavaApiTest {
    private WireMockServer mock;
    // Test data
    private final RandomGenerator randomGenerator = new RandomGenerator();
    private String randomString1;
    private String randomString2;
    private final String mock2RequestBody = "{ a: 'a', b: 'b' }";
    private int mock4ResponseDelaySeconds;

    @BeforeTest
    public void setupWireMockServer() {
        WireMockConfiguration wireMockConfiguration = new WireMockConfiguration();
        mock = new WireMockServer(wireMockConfiguration);
        mock.start();
    }

    @BeforeClass
    public void setupMock1() {
        randomString1 = randomGenerator.randomAlphanumeric(100);
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

    @BeforeClass
    public void setupMock2() {
        // Mock #2
        mock.stubFor(
            get(urlPathMatching("/jsontext/mapping2*"))
                .withQueryParam("testqueryparam", WireMock.equalTo("*"))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withBody(mock2RequestBody)
                    .withHeader("Content-Type", "application/json")
                )
        );
    }

    @BeforeClass
    public void setupMock3() {
        randomString2 = randomGenerator.randomAlphanumeric(20);
        mock.stubFor(
            post("/jsontext/mapping3")
                .atPriority(10)
                .withHeader("CustomType", WireMock.equalTo("CustomValue"))
                .withRequestBody(containing("TestValue1"))
            .willReturn(serverError()
                .withStatus(500)
                .withHeader("Content-Type", "text/plain")
                .withBody(randomString2)
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

    @BeforeClass
    public void setupMock4() {
        mock4ResponseDelaySeconds = 10;
        mock.stubFor(
            put(urlPathMatching("/*"))
                .willReturn(
                        temporaryRedirect("/plaintext/mapping1")
                        .withStatus(303)
                        .withFixedDelay((int) TimeUnit.SECONDS.toMillis(mock4ResponseDelaySeconds))
                )
        );
    }

    @Test
    public void testEndPoint1() {
        when()
            .get("/plaintext/mapping1")
        .then()
            .statusCode(200)
        .assertThat()
            .body(equalTo(randomString1))
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
        .assertThat()
            .body(equalTo(mock2RequestBody))
            .header("Content-Type", "application/json");
    }

    @Test
    public void testEndPoint3_RightBodyHeader() {
        given()
                .header("CustomType","CustomValue")
                .body("TestValue1")
        .when()
                .post("/jsontext/mapping3")
        .then()
                .statusCode(500)
        .assertThat()
                .header("Content-Type", "text/plain")
                .body(equalTo(randomString2));
    }

    @Test
    public void testEndPoint3_WrongBodyNotContainedValue() {
        given()
                .header("CustomType","CustomValue")
                .body("TestValue31")
        .when()
                .post("/jsontext/mapping3")
        .then()
                .statusCode(404);
    }

    @Test
    public void testEndPoint3_WrongHeaderAbsent() {
        given()
                .body("TestValue1")
        .when()
                .post("/jsontext/mapping3")
        .then()
                .statusCode(404);
    }

    @Test
    public void testEndPoint3_WrongHeaderIncorrectValue() {
        given()
                .header("CustomType","CustomValueChanged")
                .body("TestValue1")
        .when()
                .post("/jsontext/mapping3")
        .then()
                .statusCode(404);
    }

    @Test
    public void testEndPoint4() {
        given()
                .config(config()
                        .redirect(redirectConfig().followRedirects(false)))
        .when()
                .put("/")
        .then()
                .statusCode(303)
        .assertThat()
                .header("Location", "/plaintext/mapping1")
                .time(greaterThanOrEqualTo((long) mock4ResponseDelaySeconds), TimeUnit.SECONDS);
    }

    @AfterClass
    public void tearDownPrintEvents() {
        List<ServeEvent> allServeEvents = getAllServeEvents();
        for (ServeEvent event : allServeEvents) {
            log.debug("\nRequest {}\n\nResponse {}", event.getRequest(), event.getResponseDefinition());
        }
    }

    @AfterTest
    public void tearDownWireMockServer() {
        List<StubMapping> stubMappings = mock.getStubMappings();
        stubMappings.forEach(stub -> {
            log.debug("DELETE \n" + stub);
            removeStub(stub);
        });
        mock.stop();
    }
}
