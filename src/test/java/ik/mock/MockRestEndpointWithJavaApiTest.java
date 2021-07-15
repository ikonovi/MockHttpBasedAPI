package ik.mock;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.stubbing.ServeEvent;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import ik.util.random.RandomGenerator;
import lombok.extern.log4j.Log4j2;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.restassured.RestAssured.*;
import static io.restassured.config.RedirectConfig.redirectConfig;
import static org.apache.http.HttpHeaders.CONTENT_TYPE;
import static org.apache.http.HttpHeaders.LOCATION;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

@Log4j2
public class MockRestEndpointWithJavaApiTest extends TestBase {
    // Test data
    private final RandomGenerator randomGenerator = new RandomGenerator();
    private String mock1ResponseBody;
    private String mock2ResponseBody = "{ a: \"a\", b: \"b\" }";
    private String mock3ResponseBody;
    private long mock4ResponseDelaySeconds;

    @BeforeClass
    public void setupMock1() {
        mock1ResponseBody = randomGenerator.randomAlphanumeric(100);
        mock.stubFor(
            get("/plaintext/mapping1")
                .willReturn(aResponse()
                    .withStatus(200)
                    .withBody(mock1ResponseBody)
                    .withHeader("Content-Type", "text/plain")
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
                    .withBody(mock2ResponseBody)
                    .withHeader("Content-Type", "application/json")
                )
        );
    }

    @BeforeClass
    public void setupMock3() {
        mock3ResponseBody = randomGenerator.randomAlphanumeric(20);
        mock.stubFor(
            post("/jsontext/mapping3")
                .atPriority(10)
                .withHeader("CustomType", WireMock.equalTo("CustomValue"))
                .withRequestBody(containing("TestValue1"))
            .willReturn(serverError()
                .withStatus(500)
                .withHeader("Content-Type", "text/plain")
                .withBody(mock3ResponseBody)
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
        mock4ResponseDelaySeconds = 10L;
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
            .body(equalTo(mock1ResponseBody))
            .header(CONTENT_TYPE, "text/plain");
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
            .body(equalTo(mock2ResponseBody))
            .header(CONTENT_TYPE, "application/json");
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
                .header(CONTENT_TYPE, "text/plain")
                .body(equalTo(mock3ResponseBody));
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
                .header(LOCATION, "/plaintext/mapping1")
                .time(greaterThanOrEqualTo(mock4ResponseDelaySeconds), TimeUnit.SECONDS);
    }

    //@AfterClass
    public void tearDownPrintEvents() {
        List<ServeEvent> allServeEvents = getAllServeEvents();
        for (ServeEvent event : allServeEvents) {
            log.debug("\nRequest {}\n\nResponse {}", event.getRequest(), event.getResponseDefinition());
        }
    }

    @AfterClass
    public void printStubs() {
        List<StubMapping> stubMappings = mock.getStubMappings();
        stubMappings.forEach(stub -> {
            log.debug("STUB:\n{}\n", stub);
        });
    }
}
