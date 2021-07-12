package ik.mock.admin.mapping;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Response {
    private Integer status;
    private String body;
    private Header headers;
}
