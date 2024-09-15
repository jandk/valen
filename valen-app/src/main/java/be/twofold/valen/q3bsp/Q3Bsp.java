package be.twofold.valen.q3bsp;

import be.twofold.valen.q3bsp.model.*;

import java.util.*;

public record Q3Bsp(
    String entities,
    List<Shader> shaders,
    List<Plane> planes,
    List<Node> nodes,
    List<Leaf> leafs,
    List<Integer> leafSurfaces,
    List<Integer> leafBrushes,
    List<Model> models,
    List<Brush> brushes,
    List<BrushSide> brushSides,
    List<DrawVert> drawVerts,
    List<Integer> drawIndexes,
    List<Fog> fogs,
    List<Surface> surfaces,
    List<LightMap> lightMaps,
    List<LightGrid> lightGrid,
    Visibility visibility
) {
    @Override
    public String toString() {
        return Objects.toIdentityString(this);
    }
}
