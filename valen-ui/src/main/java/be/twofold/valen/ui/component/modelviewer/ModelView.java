package be.twofold.valen.ui.component.modelviewer;

import be.twofold.valen.core.math.*;
import javafx.scene.shape.*;

import java.util.*;

public interface ModelView {
    void setMeshes(List<TriangleMesh> meshes, Axis upAxis);
}
