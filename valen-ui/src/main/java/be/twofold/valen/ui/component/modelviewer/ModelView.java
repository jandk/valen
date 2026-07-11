package be.twofold.valen.ui.component.modelviewer;

import be.twofold.valen.core.geometry.*;
import be.twofold.valen.ui.component.*;

import java.util.*;

public interface ModelView {
    void setMeshes(List<MeshMaterial> meshesAndMaterials, Axis upAxis);

    /**
     * A mesh paired with its optional decoded albedo map. The geometry is the
     * neutral core {@link Mesh}; the view builds the JavaFX mesh and material.
     */
    record MeshMaterial(
        Mesh mesh,
        Optional<DecodedImage> diffuse
    ) {
    }
}
