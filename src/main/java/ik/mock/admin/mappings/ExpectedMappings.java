package ik.mock.admin.mappings;

import ik.mock.admin.mappings.entity.Mapping;
import lombok.Data;

@Data
public class ExpectedMappings {
    private Mapping mapping1;
    private Mapping mapping2;
    private Mapping mapping3Se500;
    private Mapping mapping3Nf404;
    private Mapping redirectToMapping1;
}
