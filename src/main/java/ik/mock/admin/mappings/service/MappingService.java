package ik.mock.admin.mappings.service;

import ik.mock.admin.mappings.entity.Mapping;
import ik.mock.exceptions.TestsExecutionException;
import ik.util.random.RandomGenerator;

import java.util.List;
import java.util.Optional;

public class MappingService {
    private final RandomGenerator randomGenerator;

    public MappingService() {
        this.randomGenerator = new RandomGenerator();
    }

    public void customizeMapping1(Mapping mapping){
        String responseBody = randomGenerator.randomAlphanumeric(100);
        mapping.getResponse().setBody(responseBody);
    }

    public static Mapping find(String mappingName, List<Mapping> mappings) throws TestsExecutionException {
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
}
