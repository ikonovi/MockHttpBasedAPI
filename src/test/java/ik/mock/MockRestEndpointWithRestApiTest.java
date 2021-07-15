package ik.mock;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ik.mock.admin.mappings.StubMappings;
import ik.mock.admin.mappings.entity.AllStubMappings;
import ik.mock.admin.mappings.entity.Mapping;
import ik.mock.config.TestsConfigReader;
import ik.mock.exceptions.TestsExecutionException;
import ik.resources.JsonResource;
import ik.util.random.RandomGenerator;
import lombok.extern.log4j.Log4j2;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.List;

import static io.restassured.RestAssured.when;
import static org.hamcrest.CoreMatchers.equalTo;

@Log4j2
public class MockRestEndpointWithRestApiTest extends TestBase {
    private final RandomGenerator randomGenerator = new RandomGenerator();
    private Mapping stubMapping1;

    @BeforeTest
    public void setupMock1() {
        // read
        JsonResource<AllStubMappings> jsonResource = new JsonResource<>();
        String jsonPath = TestsConfigReader.getTestsConfig().getMockProps().getMappingJsonPath();
        try {
            List<Mapping> mappings = jsonResource.deserialize(jsonPath, AllStubMappings.class).getMappings();
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            Mapping mapping1 = mappings.get(0);
            // update
            String mock1ResponseBody = randomGenerator.randomAlphanumeric(100);
            mapping1.getResponse().setBody(mock1ResponseBody);
            String mapping1json = gson.toJson(mapping1);
            // init
            StubMappings stubMappings = new StubMappings();
            stubMapping1 = stubMappings.createStubMapping(mapping1json);
        } catch (TestsExecutionException e) {
            Assert.fail("Failed mock setup", e);
        }
    }


    @Test
    public void testEndPoint1() {
        when()
                .get(stubMapping1.getRequest().getUrl())
        .then()
                .statusCode(200)
        .assertThat()
                .body(equalTo(stubMapping1.getResponse().getBody()))
                .headers(stubMapping1.getResponse().getHeaders());
    }
}
