package ik.mock.admin.mappings.entity;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import java.util.Map;

@Data
@Setter(AccessLevel.NONE)
public class Response {
    private Integer status;
    @Setter
    private String body;
    private Map<String, Object> headers; // key value can be type of String or List<String>.
    private Integer fixedDelayMilliseconds;
}
