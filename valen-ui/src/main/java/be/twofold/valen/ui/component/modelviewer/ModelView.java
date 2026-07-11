package be.twofold.valen.ui.component.modelviewer;

import be.twofold.valen.core.geometry.*;
import wtf.reversed.toolbox.collect.*;

import java.util.*;

public interface ModelView {
    void setMeshes(List<MeshMaterial> meshesAndMaterials, Axis upAxis);

    /**
     * A mesh paired with its optional decoded albedo map. The geometry is the
     * neutral core {@link Mesh}; the view builds the JavaFX mesh and material.
     */
    record MeshMaterial(
        Mesh mesh,
        Optional<DiffuseMap> diffuse
    ) {
    }

    /**
     * A decoded albedo image: BGRA pixels with premultiplied alpha, stride
     * {@code width * 4}.
     */
    record DiffuseMap(
        int width,
        int height,
        Bytes.Mutable pixels
    ) {
    }
}
