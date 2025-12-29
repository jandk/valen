package be.twofold.valen.game.goldsrc.reader.mdl.v10;

import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.util.*;

public record StudioMesh(
    int skinRef,
    List<StudioStrip> strips,
    int triangleCount,
    int vertexOffset,
    int vertexCount,
    int normalsOffset,
    int normalsCount
) {
    public static StudioMesh read(BinarySource source) throws IOException {
        var triangleCount = source.readInt();
        var triangleOffset = source.readInt();
        var skinRef = source.readInt();
        var normalCount = source.readInt();
        var normalOffset = source.readInt();

        var vertexOffset = 0;
        var vertexMax = 0;
        var vertexCount = 0;

        source.position(triangleOffset);
        var strips = new ArrayList<StudioStrip>();
        while (true) {
            var strip = StudioStrip.read(source);
            if (strip == null) {
                break;
            }
            var stats = strip.tris().stream()
                .mapToInt(StudioTriVert::vertIndex)
                .summaryStatistics();
            vertexOffset = Math.min(vertexOffset, stats.getMin());
            vertexMax = Math.max(vertexMax, stats.getMax());

            strips.add(strip);
        }
        vertexCount = vertexMax - vertexOffset;
        return new StudioMesh(skinRef, strips, triangleCount, vertexOffset, vertexCount, normalOffset, normalCount);
    }
}
