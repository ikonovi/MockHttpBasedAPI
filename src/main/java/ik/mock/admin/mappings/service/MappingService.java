package ik.mock.admin.mappings.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ik.mock.admin.mappings.testing.ExpectedMappings;
import ik.mock.admin.mappings.entity.AllMappings;
import ik.mock.admin.mappings.entity.Mapping;
import ik.mock.config.TestsConfigReader;
import ik.mock.config.entity.TestsConfigProps;
import ik.mock.exceptions.JsonResourceDeserializationException;
import ik.mock.exceptions.MappingHttpServiceException;
import ik.mock.exceptions.TestsExecutionException;
import ik.resources.JsonResource;
import ik.util.random.RandomGenerator;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Log4j2
public class MappingService {
    private final RandomGenerator randomGenerator;
    private final Gson gson;
    private final MappingHttpService mappingHttpService;
    private final List<Mapping> createdMappings;

    public MappingService() {
        this.randomGenerator = new RandomGenerator();
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.mappingHttpService = new MappingHttpService();
        this.createdMappings = new ArrayList<>();
    }

    public ExpectedMappings customizeAndCreateMappings() throws TestsExecutionException {
        List<Mapping> deserializedMappings = deserializedMappingsFromJsonResource();
        ExpectedMappings expectedMappings = new ExpectedMappings();

        Mapping mapping1 = find("Mapping1", deserializedMappings);
        customizeMapping1(mapping1);
        expectedMappings.setMapping1(mapping1);
        String mapping1Json = gson.toJson(mapping1);
        createdMappings.add(mappingHttpService.createStubMapping(mapping1Json));

        Mapping mapping2 = find("Mapping2", deserializedMappings);
        customizeMapping2(mapping2);
        expectedMappings.setMapping2(mapping2);
        String mapping2Json = gson.toJson(mapping2);
        createdMappings.add(mappingHttpService.createStubMapping(mapping2Json));

        Mapping mapping3Se500 = find("Mapping3 - Server Error 500", deserializedMappings);
        customizeMapping3ServerError500(mapping3Se500);
        expectedMappings.setMapping3Se500(mapping3Se500);
        String mapping3Se500Json = gson.toJson(mapping3Se500);
        createdMappings.add(mappingHttpService.createStubMapping(mapping3Se500Json));

        Mapping mapping3Nf404 = find("Mapping3 - Not Found 404", deserializedMappings);
        expectedMappings.setMapping3Nf404(mapping3Nf404);
        String mapping3Nf404Json = gson.toJson(mapping3Nf404);
        createdMappings.add(mappingHttpService.createStubMapping(mapping3Nf404Json));

        Mapping redirectToMapping1 = find("Redirect to Mapping1", deserializedMappings);
        expectedMappings.setRedirectToMapping1(redirectToMapping1);
        String redirectToMapping1Json = gson.toJson(redirectToMapping1);
        createdMappings.add(mappingHttpService.createStubMapping(redirectToMapping1Json));

        return expectedMappings;
    }

    public List<Mapping> deserializedMappingsFromJsonResource() throws JsonResourceDeserializationException {
        String mappingJsonPath = TestsConfigReader.getTestsConfig().getMockProps().getMappingJsonPath();
        JsonResource<AllMappings> jsonResource = new JsonResource<>();
        return jsonResource.deserialize(mappingJsonPath, AllMappings.class).getMappings();
    }

    public void deleteAllStubMappings() throws MappingHttpServiceException {
        this.mappingHttpService.deleteAllStubMappings();
    }

    public void printAllRequests(){
        try {
            String requestsJson = mappingHttpService.getAllRequestsInJournal();
            log.info( "================================================================"
                    + "All Requests and Responses:"
            + "================================================================");
            printInLogs(requestsJson);
        } catch (MappingHttpServiceException exception) {
            log.error("Could not print requests", exception);
        }
    }

    private void printInLogs(String json) {
        log.info("{}", json);
    }

    private static Mapping find(String mappingName, List<Mapping> mappings) throws TestsExecutionException {
        Optional<Mapping> optionalMapping = mappings.stream()
                .filter(m -> m.getName().equals(mappingName))
                .findAny();
        if(optionalMapping.isPresent()) {
            return optionalMapping.get();
        } else {
            throw new TestsExecutionException("Mapping with name " + mappingName + " was not found in list: " +
                    mappings);
        }
    }

    private void customizeMapping1(Mapping mapping){
        String responseBody = randomGenerator.randomAlphanumeric(100);
        mapping.getResponse().setBody(responseBody);
    }

    private void customizeMapping2(Mapping mapping){
        TestsConfigProps testsConfig = TestsConfigReader.getTestsConfig();
        String responseBody = gson.toJson(testsConfig);
        mapping.getResponse().setBody(responseBody);
    }

    private void customizeMapping3ServerError500(Mapping mapping){
        String responseBody = randomGenerator.randomAlphanumeric(20);
        mapping.getResponse().setBody(responseBody);
    }
}
