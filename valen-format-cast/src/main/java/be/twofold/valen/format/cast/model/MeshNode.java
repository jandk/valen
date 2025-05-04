package be.twofold.valen.format.cast.model;

import org.immutables.value.*;

import java.nio.*;
import java.util.*;

@SchemaStyle
@Value.Immutable
public non-sealed interface MeshNode extends Node {

    Optional<String> getName();

    FloatBuffer getVertexPositionBuffer();

    Buffer getFaceBuffer();

}
