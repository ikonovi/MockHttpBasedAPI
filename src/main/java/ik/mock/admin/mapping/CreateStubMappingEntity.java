package ik.mock.admin.mapping;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateStubMappingEntity {
    private Request request;
    private Response response;
    private Integer priority;
}
