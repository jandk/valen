package be.twofold.valen.reader.geometry;

import be.twofold.valen.geometry.*;

public interface LodInfo {

    int numVertices();

    int numEdges();

    int flags();

    Vector3 vertexOffset();

    float vertexScale();

    Vector2 uvOffset();

    float uvScale();

}
