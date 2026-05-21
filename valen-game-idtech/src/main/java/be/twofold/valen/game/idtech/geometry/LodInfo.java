package be.twofold.valen.game.idtech.geometry;

import wtf.reversed.toolbox.math.*;

public interface LodInfo {

    int numVertices();

    int numFaces();

    int vertexMask();

    Vector3 vertexOffset();

    float vertexScale();

    Vector2 uvOffset();

    float uvScale();

}
