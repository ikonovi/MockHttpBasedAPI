package ik.mock.admin.mapping;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Request {
    private String url;
    private String method;
    private CustomType headers;
    @SerializedName("testqueryparam")
    private TestQueryParam testQueryParam;
    //@Singular
    //private List<> bodyPatterns;
}
