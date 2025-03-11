package be.twofold.valen.ui.component.modelviewer;

import be.twofold.valen.core.math.*;
import be.twofold.valen.ui.common.*;
import jakarta.inject.*;
import javafx.beans.property.*;
import javafx.geometry.*;
import javafx.geometry.Bounds;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.transform.*;

import java.util.*;
import java.util.stream.*;

public final class ModelFXView implements ModelView, FXView {
    private final ObjectProperty<SubScene> subSceneProperty = new SimpleObjectProperty<>();
    private final Pane view = new SubSceneResizer(subSceneProperty);
    private final Group root = new Group();

    @Inject
    public ModelFXView() {
        var subScene = new SubScene(root, 400, 400, true, SceneAntialiasing.BALANCED);
        subScene.setFill(new Color(0.2, 0.2, 0.2, 1.0));
        subSceneProperty.set(subScene);
        var cameraSystem = new CameraSystem(subScene);
        root.getChildren().add(cameraSystem.camera());
    }

    @Override
    public Parent getFXNode() {
        return view;
    }

    @Override
    public void setMeshes(List<MeshMaterial> meshesAndMaterials, Axis upAxis) {
        root.getChildren().subList(1, root.getChildren().size()).clear();
        if (meshesAndMaterials.isEmpty()) {
            return;
        }

        var meshViews = meshesAndMaterials.stream()
            .map(this::toMeshView)
            .toList();

        center(meshViews, upAxis);

        root.getChildren().addAll(meshViews);
    }

    private MeshView toMeshView(MeshMaterial mesh) {
        var meshView = new MeshView(mesh.mesh());
        mesh.material().ifPresent(meshView::setMaterial);
        meshView.setCullFace(CullFace.NONE);
        return meshView;
    }

    private void center(List<MeshView> meshViews, Axis upAxis) {
        var bounds = meshViews.stream()
            .map(MeshView::getBoundsInLocal)
            .reduce(this::combine)
            .orElseThrow();

        System.out.println("Bounds " + bounds);
        double max = DoubleStream
            .of(bounds.getWidth(), bounds.getHeight(), bounds.getDepth())
            .max().getAsDouble();

        // TODO: Figure out if we can make this a bit less arbitrary
        double scale = 100.0 / max / 2.0;

        var rotation = switch (upAxis) {
            case X -> throw new UnsupportedOperationException();
            case Y -> new Rotate(180, Rotate.X_AXIS);
            case Z -> new Rotate(+90, Rotate.X_AXIS);
        };

        for (MeshView meshView : meshViews) {
            meshView.getTransforms().addAll(
                new Scale(scale, scale, scale),
                rotation,
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
