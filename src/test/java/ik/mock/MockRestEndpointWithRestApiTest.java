package ik.mock;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ik.mock.admin.mappings.service.MappingHttpService;
import ik.mock.admin.mappings.service.MappingService;
import ik.mock.admin.mappings.entity.AllMappings;
import ik.mock.admin.mappings.entity.Mapping;
import ik.mock.config.TestsConfigReader;
import ik.mock.exceptions.JsonResourceDeserializationException;
import ik.mock.exceptions.TestsExecutionException;
import ik.resources.JsonResource;
import ik.util.random.RandomGenerator;
import lombok.extern.log4j.Log4j2;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.List;

import static io.restassured.RestAssured.when;
import static org.hamcrest.CoreMatchers.equalTo;

@Log4j2
public class MockRestEndpointWithRestApiTest extends TestBase {
    List<Mapping> deserializedMappings;
    Mapping mapping1;
    Mapping mapping2;
    Mapping mapping3;
    Mapping mapping4;
    MappingHttpService mappingHttpService;
    MappingService mappingService;
    RandomGenerator randomGenerator;
    Gson gson;

    @BeforeTest
    public void setupDeserializeMappings() {
        String mappingJsonPath = TestsConfigReader.getTestsConfig().getMockProps().getMappingJsonPath();
        JsonResource<AllMappings> jsonResource = new JsonResource<>();
        try {
            this.deserializedMappings = jsonResource.deserialize(mappingJsonPath, AllMappings.class).getMappings();
        } catch (JsonResourceDeserializationException exception) {
            Assert.fail("Failed reading mapping configuration", exception);
        }
        this.randomGenerator = new RandomGenerator();
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.mappingHttpService = new MappingHttpService();
        this.mappingService = new MappingService();
    }

    @BeforeClass
    public void setupMock1() {
        String mappingName = "Mapping1";
        try {
            mapping1 = MappingService.find(mappingName, deserializedMappings);
            mappingService.customizeMapping1(mapping1);
            String mapping1Json = gson.toJson(mapping1);
            mapping1 = mappingHttpService.createStubMapping(mapping1Json);
        } catch (TestsExecutionException ex) {
            Assert.fail("Failed mock1 setup", ex);
        }
    }


    @Test
    public void testEndPoint1() {
        when()
                .get(mapping1.getRequest().getUrl())
        .then()
                .statusCode(200)
        .assertThat()
                .body(equalTo(mapping1.getResponse().getBody()))
                .headers(mapping1.getResponse().getHeaders());
    }
}
