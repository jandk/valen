package be.twofold.valen.reader.geometry;

import be.twofold.valen.core.math.*;

public interface LodInfo {

    int numVertices();

    int numFaces();

    int vertexMask();

    Vector3 vertexOffset();

    float vertexScale();

    Vector2 uvOffset();

    float uvScale();

}
