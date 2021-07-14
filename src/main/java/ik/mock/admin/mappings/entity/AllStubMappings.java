package ik.mock.admin.mappings.entity;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import java.util.List;

@Data
@Setter(AccessLevel.NONE)
public class AllStubMappings {
    private List<Mapping> mappings;
}
