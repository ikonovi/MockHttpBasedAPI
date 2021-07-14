package ik.mock.admin.mappings.entity;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@Data
@Setter(AccessLevel.NONE)
public class TestQueryParam {
    private String equalTo;
}
