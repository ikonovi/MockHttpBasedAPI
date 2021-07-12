package ik.mock.admin.mapping;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Header {
    @SerializedName("Content-Type")
    private String contentType;
    private CustomType customType;
}
