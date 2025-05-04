package be.twofold.valen.format.cast.model;

import org.immutables.value.*;

import java.util.*;

@SchemaStyle
@Value.Immutable
public non-sealed interface ModelNode extends Node {

    Optional<String> getName();

    List<MeshNode> getMeshes();

}
