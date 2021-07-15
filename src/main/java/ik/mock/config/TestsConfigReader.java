package ik.mock.config;

import ik.mock.config.entity.TestsConfigProps;
import ik.mock.exceptions.JsonResourceDeserializationExceptionNot;
import ik.resources.JsonResource;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class TestsConfigReader {

    private static TestsConfigProps properties;

    static {
        JsonResource<TestsConfigProps> jsonResource = new JsonResource<>();
        String jsonPath = "./tests_config.json";
        try {
            properties = jsonResource.deserialize(jsonPath, TestsConfigProps.class);
        } catch (JsonResourceDeserializationExceptionNot ex) {
            log.error("Failed reading test configuration from file \"" + jsonPath + "\"", ex);
        }
    }

    public static TestsConfigProps getTestsConfig() {
        log.debug("{}", properties);
        return properties;
    }
}
