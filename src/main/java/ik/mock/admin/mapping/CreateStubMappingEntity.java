package ik.mock.admin.mapping;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateStubMappingEntity {
    Request request;
    Response response;
    Integer priority;
}