package be.twofold.valen.export.gltf;

import be.twofold.valen.core.geometry.*;
import com.google.gson.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.*;
import wtf.reversed.toolbox.collect.*;

import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;

import static org.assertj.core.api.Assertions.*;

class BlendShapeExportTest {

    @Test
    void accessorBoundsAreFinite(@TempDir Path dir) throws Exception {
        var gltf = exportTriangleWithBlendShape(dir);

        for (var accessor : gltf.getAsJsonArray("accessors")) {
            assertFinite(accessor.getAsJsonObject(), "min");
            assertFinite(accessor.getAsJsonObject(), "max");
        }
    }

    @Test
    void morphTargetAccessorSpansTheVertices(@TempDir Path dir) throws Exception {
        var gltf = exportTriangleWithBlendShape(dir);

        var targets = gltf.getAsJsonArray("meshes").get(0).getAsJsonObject()
            .getAsJsonArray("primitives").get(0).getAsJsonObject()
            .getAsJsonArray("targets");
        assertThat(targets).as("a morph target is written").hasSize(1);

        var index = targets.get(0).getAsJsonObject().get("POSITION").getAsInt();
        var accessor = gltf.getAsJsonArray("accessors").get(index).getAsJsonObject();

        assertThat(accessor.has("sparse")).as("morph targets are written sparsely").isTrue();
        assertThat(accessor.get("count").getAsInt())
            .as("morph target accessor spans the vertices, not the faces")
            .isEqualTo(3);
        assertThat(accessor.getAsJsonObject("sparse").get("count").getAsInt())
            .as("one displaced vertex")
            .isEqualTo(1);
    }

    /**
     * Gson parses leniently, so {@code Infinity} and {@code NaN} arrive as non finite doubles.
     */
    private static void assertFinite(JsonObject accessor, String name) {
        var bounds = accessor.getAsJsonArray(name);
        if (bounds == null) {
            return;
        }
        for (var bound : bounds) {
            assertThat(bound.getAsDouble())
                .as("accessor %s must be finite", name)
                .isFinite();
        }
    }

    private static JsonObject exportTriangleWithBlendShape(Path dir) throws Exception {
        var model = new Model(List.of(triangleWithBlendShape()), Optional.empty(),
            Optional.of("test"), Optional.empty(), Axis.Z);

        var path = dir.resolve("test.gltf");
        var exporter = new GltfModelExporter();
        exporter.setProperty("gltf.mode", "gltf");
        exporter.export(model, path);

        return JsonParser.parseString(Files.readString(path, StandardCharsets.UTF_8)).getAsJsonObject();
    }

    private static Mesh triangleWithBlendShape() {
        var indices = Ints.Mutable.allocate(3);
        indices.set(0, 0);
        indices.set(1, 1);
        indices.set(2, 2);

        var positions = Floats.Mutable.allocate(9);
        positions.copyFrom(new float[]{0, 0, 0, 1, 0, 0, 0, 1, 0});

        var values = Floats.Mutable.allocate(3);
        values.copyFrom(new float[]{0.5f, 0.25f, 0.125f});
        var shapeIndices = Shorts.Mutable.allocate(1);
        shapeIndices.set(0, (short) 2);

        return Mesh.builder(indices, 3)
            .position(positions)
            .blendShapes(List.of(new BlendShape("shape", values, shapeIndices)))
            .build();
    }
}
