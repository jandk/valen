package be.twofold.valen.ui.viewer.model;

import be.twofold.valen.ui.*;
import jakarta.inject.*;
import javafx.beans.property.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.shape.*;

import java.util.*;

public final class ModelViewFx extends AbstractView<ModelViewListener> implements ModelView {
    private final ObjectProperty<SubScene> subSceneProperty = new SimpleObjectProperty<>();
    private final Pane view = new SubSceneResizer(subSceneProperty);
    private final Group root = new Group();

    @Inject
    public ModelViewFx() {
        super(ModelViewListener.class);
    }

    @Override
    public Parent getView() {
        return view;
    }

    @Override
    public void setMeshes(List<TriangleMesh> meshes) {
        var meshViews = meshes.stream()
            .map(MeshView::new)
            .toList();

        var subScene = new SubScene(root, 400, 400, true, SceneAntialiasing.BALANCED);
        root.getChildren().addAll(meshViews);
        subSceneProperty.set(subScene);
    }
}
