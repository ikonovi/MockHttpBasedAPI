package ik.mock.config.entity;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@Data
@Setter(AccessLevel.NONE)
public class MockProps {
    String adminEndpointProtocol;
    String adminEndpointHost;
    Integer adminEndpointPort;
    String endPointAdminMappingsPath;
    String endPointAdminRequestsPath;
    String mappingJsonPath;
}
