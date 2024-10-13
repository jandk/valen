package org.redeye.valen.game.spacemarines2.types.lwi;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.math.*;

import java.io.*;
import java.util.*;

public record LwiElementDataChild(
    Matrix4 mat,
    List<LwiElementDataChildSubItem> subItems,
    byte materialIndex,
    byte useInNavMesh,
    byte unk
) {

    private static Matrix4 readMat4x3(DataSource source) throws IOException {
        Vector3[] rows = new Vector3[4];

        rows[3] = source.readVector3();
        rows[0] = source.readVector3();
        rows[1] = source.readVector3();
        rows[2] = source.readVector3();

        return new Matrix4(
            rows[0].x(), rows[0].y(), rows[0].z(), 0,
            rows[1].x(), rows[1].y(), rows[1].z(), 0,
            rows[2].x(), rows[2].y(), rows[2].z(), 0,
            rows[3].x(), rows[3].y(), rows[3].z(), 1
        );
    }

    public static LwiElementDataChild read(DataSource source, int version) throws IOException {
        var mat = readMat4x3(source);
        List<LwiElementDataChildSubItem> items = new ArrayList<>();
        if (version >= 6) {
            var count = source.readInt();
            for (int i = 0; i < count; i++) {
                items.add(LwiElementDataChildSubItem.read(source));
            }
        }
        byte materialIndex = 0;
        byte useInNavmesh = 0;
        byte unk = 0;
        if (version >= 10) {
            materialIndex = source.readByte();
            if (version >= 12) {
                useInNavmesh = source.readByte();
                unk = source.readByte();
            }
        }
        return new LwiElementDataChild(mat, items, materialIndex, useInNavmesh, unk);
    }
}
