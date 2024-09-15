package be.twofold.valen.q3bsp.model;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.math.*;

import java.io.*;

public record DrawVert(
    Vector3 position,
    Vector2 texCoord0,
    Vector2 texCoord1,
    Vector3 normal,
    Color4 color
) {
    public static final int BYTES = 2 * Vector3.BYTES + 2 * Vector2.BYTES + Color4.BYTES;

    public static DrawVert read(DataSource source) throws IOException {
        Vector3 position = Vector3.read(source);
        Vector2 texCoord0 = Vector2.read(source);
        Vector2 texCoord1 = Vector2.read(source);
        Vector3 normal = Vector3.read(source);
        Color4 color = Color4.read(source);
        return new DrawVert(position, texCoord0, texCoord1, normal, color);
    }
}
