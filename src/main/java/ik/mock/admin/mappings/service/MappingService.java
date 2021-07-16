package ik.mock.admin.mappings.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ik.mock.admin.mappings.ExpectedMappings;
import ik.mock.admin.mappings.entity.Mapping;
import ik.mock.config.TestsConfigReader;
import ik.mock.config.entity.TestsConfigProps;
import ik.mock.exceptions.TestsExecutionException;
import ik.util.random.RandomGenerator;

import java.util.List;
import java.util.Optional;

public class MappingService {
    private final RandomGenerator randomGenerator;
    private Gson gson;
    private MappingHttpService mappingHttpService;

    public MappingService() {
        this.randomGenerator = new RandomGenerator();
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.mappingHttpService = new MappingHttpService();
    }

    public ExpectedMappings customizeAndCreateMappings(List<Mapping> mappings) throws TestsExecutionException {
        ExpectedMappings expectedMappings = new ExpectedMappings();

        Mapping mapping1 = find("Mapping1", mappings);
        customizeMapping1(mapping1);
        expectedMappings.setMapping1(mapping1);
        String mapping1Json = gson.toJson(mapping1);
        mappingHttpService.createStubMapping(mapping1Json);

        Mapping mapping2 = find("Mapping2", mappings);
        customizeMapping2(mapping2);
        String mapping2Json = gson.toJson(mapping2);
        Mapping createdMapping2 = mappingHttpService.createStubMapping(mapping2Json);
        expectedMappings.setMapping2(createdMapping2);

        Mapping mapping3Se500 = find("Mapping3 - Server Error 500", mappings);
        customizeMapping3_ServerError500(mapping3Se500);
        expectedMappings.setMapping3Se500(mapping3Se500);
        String mapping3Se500Json = gson.toJson(mapping3Se500);
        mappingHttpService.createStubMapping(mapping3Se500Json);

        Mapping mapping3Nf404 = find("Mapping3 - Not Found 404", mappings);
        expectedMappings.setMapping3Nf404(mapping3Nf404);
        String mapping3Nf404Json = gson.toJson(mapping3Nf404);
        mappingHttpService.createStubMapping(mapping3Nf404Json);

        Mapping redirectToMapping1 = find("Redirect to Mapping1", mappings);
        expectedMappings.setRedirectToMapping1(redirectToMapping1);
        String redirectToMapping1Json = gson.toJson(redirectToMapping1);
        mappingHttpService.createStubMapping(redirectToMapping1Json);

        return expectedMappings;
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

    private void customizeMapping3_ServerError500(Mapping mapping){
        String responseBody = randomGenerator.randomAlphanumeric(20);
        mapping.getResponse().setBody(responseBody);
    }
}
