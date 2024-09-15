package be.twofold.valen.ui.viewer.model;

import be.twofold.valen.ui.*;
import jakarta.inject.*;
import javafx.beans.property.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.transform.*;

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

        center(meshViews);

        var subScene = new SubScene(root, 400, 400, true, SceneAntialiasing.BALANCED);
        subScene.setFill(new Color(0.2, 0.2, 0.2, 1.0));
        subSceneProperty.set(subScene);

        var cameraSystem = new CameraSystem(subScene);
        root.getChildren().add(cameraSystem.camera());
        root.getChildren().addAll(meshViews);
    }

    private void center(List<MeshView> meshViews) {
        var bounds = meshViews.stream()
            .map(MeshView::getBoundsInLocal)
            .reduce(this::combine)
            .orElseThrow();

        for (MeshView meshView : meshViews) {
            meshView.getTransforms().addAll(
                new Rotate(-90, Rotate.X_AXIS),
                new Translate(
                    -((bounds.getWidth() / 2) + bounds.getMinX()),
                    -((bounds.getHeight() / 2) + bounds.getMinY()),
                    -((bounds.getDepth() / 2) + bounds.getMinZ())
                )
            );
        }
    }

    private Bounds combine(Bounds first, Bounds second) {
        var minX = Math.min(first.getMinX(), second.getMinX());
        var minY = Math.min(first.getMinY(), second.getMinY());
        var minZ = Math.min(first.getMinZ(), second.getMinZ());
        var maxX = Math.max(first.getMaxX(), second.getMaxX());
        var maxY = Math.max(first.getMaxY(), second.getMaxY());
        var maxZ = Math.max(first.getMaxZ(), second.getMaxZ());
        return new BoundingBox(minX, minY, minZ, maxX - minX, maxY - minY, maxZ - minZ);
    }

}