package ik.mock;

import ik.mock.admin.mappings.testing.ExpectedMappings;
import ik.mock.admin.mappings.service.MappingService;
import ik.mock.exceptions.MappingHttpServiceException;
import ik.mock.exceptions.TestsExecutionException;
import lombok.extern.log4j.Log4j2;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.*;
import static io.restassured.config.RedirectConfig.redirectConfig;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

@Log4j2
public class MockRestEndpointWithRestApiTest extends TestBase {
    MappingService mappingService = new MappingService();
    ExpectedMappings expectedMappings;
    String endPoint3RequestBodyContainsText;

    @BeforeClass
    public void setupMocks() {
        try {
            expectedMappings = mappingService.customizeAndCreateMappings();
            endPoint3RequestBodyContainsText = expectedMappings.getMapping3Se500().getRequest().getBodyPatterns().get(0).get("contains");
        } catch (TestsExecutionException ex) {
            Assert.fail("Failed mock setup", ex);
        }
    }

    @AfterClass
    public void tearDownDeleteAllStubMappings() {
        try {
            this.mappingService.deleteAllStubMappings();
        } catch (MappingHttpServiceException exception) {
            Assert.fail("Failed to delete mock mappings", exception);
        }
    }

    @AfterClass
    public void printRequestsAndResponsesInConsole(){
        this.mappingService.printAllRequests();
    }

    @Test
    public void testEndPoint1() {
        when()
                .get(expectedMappings.getMapping1().getRequest().getUrl())
        .then()
                .statusCode(expectedMappings.getMapping1().getResponse().getStatus())
        .assertThat()
                .body(equalTo(expectedMappings.getMapping1().getResponse().getBody()))
                .headers(expectedMappings.getMapping1().getResponse().getHeaders());
    }

    @Test
    public void testEndPoint2() {
        given()
                .queryParam("testqueryparam", "*")
        .when()
                .get(expectedMappings.getMapping2().getRequest().getUrlPathPattern().replaceAll("\\*",""))
        .then()
                .statusCode(expectedMappings.getMapping2().getResponse().getStatus())
        .assertThat()
                .body(equalTo(expectedMappings.getMapping2().getResponse().getBody()))
                .headers(expectedMappings.getMapping2().getResponse().getHeaders());
    }

    @Test
    public void testEndPoint3_RightBodyAndHeader() {
        given()
                .header("CustomType","CustomValue")
                .body(endPoint3RequestBodyContainsText)
        .when()
                .post(expectedMappings.getMapping3Se500().getRequest().getUrl())
        .then()
                .statusCode(expectedMappings.getMapping3Se500().getResponse().getStatus())
        .assertThat()
                .headers(expectedMappings.getMapping3Se500().getResponse().getHeaders())
                .body(equalTo(expectedMappings.getMapping3Se500().getResponse().getBody()));
    }

    @Test
    public void testEndPoint3_BodyNotContainedValue() {
        given()
                .header("CustomType","CustomValue")
                .body(endPoint3RequestBodyContainsText.substring(0, endPoint3RequestBodyContainsText.length() - 2))
        .when()
                .post(expectedMappings.getMapping3Nf404().getRequest().getUrl())
        .then()
                .statusCode(expectedMappings.getMapping3Nf404().getResponse().getStatus());
    }

    @Test
    public void testEndPoint3_HeaderAbsent() {
        given()
                .body(endPoint3RequestBodyContainsText)
        .when()
                .post(expectedMappings.getMapping3Nf404().getRequest().getUrl())
        .then()
                .statusCode(expectedMappings.getMapping3Nf404().getResponse().getStatus());
    }

    @Test
    public void testEndPoint3_HeaderIncorrectValue() {
        given()
                .header("CustomType","CustomValue Changed")
                .body(endPoint3RequestBodyContainsText)
        .when()
                .post(expectedMappings.getMapping3Nf404().getRequest().getUrl())
        .then()
                .statusCode(expectedMappings.getMapping3Nf404().getResponse().getStatus());
    }

    @Test
    public void testEndPoint4() {
        given()
                .config(config()
                        .redirect(redirectConfig().followRedirects(false)))
        .when()
                .put(expectedMappings.getRedirectToMapping1().getRequest().getUrlPathPattern().replaceAll("\\*",""))
        .then()
                .statusCode(expectedMappings.getRedirectToMapping1().getResponse().getStatus())
        .assertThat()
                .headers(expectedMappings.getRedirectToMapping1().getResponse().getHeaders() )
                .time(greaterThanOrEqualTo(expectedMappings.getRedirectToMapping1().getResponse().getFixedDelayMilliseconds()));
    }
}
