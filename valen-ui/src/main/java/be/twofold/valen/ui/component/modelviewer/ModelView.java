package be.twofold.valen.ui.component.modelviewer;

import be.twofold.valen.core.geometry.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;

import java.util.*;

public interface ModelView {
    void setMeshes(List<MeshMaterial> meshesAndMaterials, Axis upAxis);

    record MeshMaterial(
        TriangleMesh mesh,
        Optional<Material> material
    ) {
    }
}
