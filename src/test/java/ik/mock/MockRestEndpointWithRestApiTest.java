package ik.mock;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ik.mock.admin.mappings.ExpectedMappings;
import ik.mock.admin.mappings.service.MappingHttpService;
import ik.mock.admin.mappings.service.MappingService;
import ik.mock.admin.mappings.entity.AllMappings;
import ik.mock.admin.mappings.entity.Mapping;
import ik.mock.config.TestsConfigReader;
import ik.mock.exceptions.JsonResourceDeserializationException;
import ik.mock.exceptions.TestsExecutionException;
import ik.resources.JsonResource;
import lombok.extern.log4j.Log4j2;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.*;
import static io.restassured.config.RedirectConfig.redirectConfig;
import static org.apache.http.HttpHeaders.LOCATION;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

@Log4j2
public class MockRestEndpointWithRestApiTest extends TestBase {
    MappingService mappingService;
    List<Mapping> deserializedMappings;
    ExpectedMappings expectedMappings;
    String endPoint3RequestBodyContainsText;

    @BeforeTest
    public void setupDeserializeMappings() {
        String mappingJsonPath = TestsConfigReader.getTestsConfig().getMockProps().getMappingJsonPath();
        JsonResource<AllMappings> jsonResource = new JsonResource<>();
        try {
            this.deserializedMappings = jsonResource.deserialize(mappingJsonPath, AllMappings.class).getMappings();
        } catch (JsonResourceDeserializationException exception) {
            Assert.fail("Failed reading mapping configuration", exception);
        }
        this.mappingService = new MappingService();
    }

    @BeforeClass
    public void setupMocks() {
        try {
            expectedMappings = mappingService.customizeAndCreateMappings(deserializedMappings);
            endPoint3RequestBodyContainsText = expectedMappings.getMapping3Se500().getRequest().getBodyPatterns().get(0).get("contains");
        } catch (TestsExecutionException ex) {
            Assert.fail("Failed mock1 setup", ex);
        }
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
