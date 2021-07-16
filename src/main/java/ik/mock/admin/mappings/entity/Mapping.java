package ik.mock.admin.mappings.entity;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@Data
@Setter(AccessLevel.NONE)
public class Mapping {
    private String name;
    private Request request;
    private Response response;
    private Integer priority;
}
