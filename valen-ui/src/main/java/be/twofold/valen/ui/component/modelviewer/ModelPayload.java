package be.twofold.valen.ui.component.modelviewer;

import be.twofold.valen.core.geometry.*;
import be.twofold.valen.ui.component.*;

import java.util.*;

/**
 * Display-ready result of decoding a model: each mesh paired with its decoded
 * albedo map, plus the up axis.
 */
record ModelPayload(List<MeshMaterial> meshes, Axis upAxis) {

    /**
     * A mesh paired with its optional decoded albedo map.
     */
    record MeshMaterial(Mesh mesh, Optional<DecodedImage> diffuse) {
    }
}
