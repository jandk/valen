package be.twofold.valen.ui.viewer.model;

import be.twofold.valen.ui.*;
import javafx.scene.shape.*;

import java.util.*;

public interface ModelView extends View<ModelViewListener> {
    void setMeshes(List<TriangleMesh> meshes);
}
