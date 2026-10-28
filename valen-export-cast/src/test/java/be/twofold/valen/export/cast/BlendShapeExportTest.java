package be.twofold.valen.export.cast;

import be.twofold.tinycast.*;
import be.twofold.valen.core.geometry.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.*;
import wtf.reversed.toolbox.collect.*;

import java.nio.file.*;
import java.util.*;

import static org.assertj.core.api.Assertions.*;

class BlendShapeExportTest {

    @Test
    @Disabled
    void blendShapeKeepsItsData(@TempDir Path dir) throws Exception {
        var model = new Model(List.of(triangleWithBlendShape()), Optional.empty(),
            Optional.of("test"), Optional.empty(), Axis.Z);

        var path = dir.resolve("test.cast");
        new CastModelExporter().export(model, path);

        Cast cast;
        try (var in = Files.newInputStream(path)) {
            cast = Cast.read(in);
        }

        var shapes = cast.getRootNodes().stream()
            .filter(CastNodes.Root.class::isInstance)
            .map(CastNodes.Root.class::cast)
            .flatMap(root -> root.getModels().stream())
            .flatMap(m -> m.getBlendShapes().stream())
            .toList();

        assertThat(shapes).as("one blend shape node").hasSize(1);

        var shape = shapes.getFirst();
        assertThat(shape.getTargetShapeVertexPositions())
            .as("positions must survive the write")
            .isNotNull();
        assertThat(shape.getTargetShapeVertexPositions().remaining())
            .as("one displaced vertex, three floats")
            .isEqualTo(3);
        assertThat(shape.getTargetShapeVertexIndices().remaining())
            .as("one vertex index")
            .isEqualTo(1);
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
        shapeIndices.set(0, (short) 0);

        return Mesh.builder(indices, 3)
            .position(positions)
            .blendShapes(List.of(new BlendShape("shape", values, shapeIndices)))
            .build();
    }
}
