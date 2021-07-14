package ik.mock.admin.mappings.entity;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Data
@Setter(AccessLevel.NONE)
public class Request {
    private String url;
    private String urlPathPattern;
    private String method;
    private Map<String, Map<String, String>> queryParameters;
    private Map<String, Object> headers;
    private List<Map<String, String>> bodyPatterns;
}
