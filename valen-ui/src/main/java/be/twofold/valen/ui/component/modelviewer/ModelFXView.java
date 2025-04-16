package be.twofold.valen.ui.component.modelviewer;

import be.twofold.valen.core.math.*;
import be.twofold.valen.ui.common.*;
import jakarta.inject.*;
import javafx.beans.property.*;
import javafx.geometry.*;
import javafx.geometry.Bounds;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.transform.*;

import java.util.*;
import java.util.stream.*;

public final class ModelFXView implements ModelView, FXView {
    private final ObjectProperty<SubScene> subSceneProperty = new SimpleObjectProperty<>();
    private final VBox view = new VBox();
    private final Group root = new Group();
    private final Label statusLabel = new Label();
    private final CameraSystem cameraSystem;

    @Inject
    public ModelFXView() {
        var subScene = new SubScene(root, 400, 400, true, SceneAntialiasing.BALANCED);
        subScene.setFill(new Color(0.2, 0.2, 0.2, 1.0));
        subSceneProperty.set(subScene);
        this.cameraSystem = new CameraSystem(subScene);
        root.getChildren().add(cameraSystem.camera());

        var separatorPane = new Pane();
        HBox.setHgrow(separatorPane, Priority.ALWAYS);

        var toolBar = new ToolBar();
        toolBar.getItems().add(separatorPane);
        toolBar.getItems().add(statusLabel);

        var subScenePane = new SubScenePane(subSceneProperty);
        VBox.setVgrow(subScenePane, Priority.ALWAYS);
        view.getChildren().add(toolBar);
        view.getChildren().add(subScenePane);
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
        cameraSystem.reset();

        var numVertices = meshesAndMaterials.stream().mapToInt(mm -> mm.mesh().getPoints().size()).sum() / 3;
        var numFaces = meshesAndMaterials.stream().mapToInt(mm -> mm.mesh().getFaces().size()).sum() / 9;
        statusLabel.setText("Meshes: " + meshViews.size() + ", Vertices: " + numVertices + ", Faces: " + numFaces);

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
        double scale = 100.0 / max;

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
