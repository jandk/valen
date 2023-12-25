package be.twofold.valen.reader.decl.entities;

public record MaterialPassCommonFields(
    ToolsVisibilityMask toolsVisibility,
    int passSortBias,
    Integer localSortBias,
    float depthSortBias
) {
}
