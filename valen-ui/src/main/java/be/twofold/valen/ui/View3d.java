package be.twofold.valen.ui;

import javafx.animation.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.transform.*;
import javafx.util.*;

public class View3d extends Pane {
    public View3d() {
        buildView();
    }

    private void buildView() {
        PhongMaterial material = new PhongMaterial();

        Shape3D earth = new Box(5, 5, 5);
        earth.setMaterial(material);
        earth.setRotationAxis(Rotate.Y_AXIS);

        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.getTransforms().addAll(
            new Rotate(-20, Rotate.Y_AXIS),
            new Rotate(-20, Rotate.X_AXIS),
            new Translate(0, 0, -20));

        Group root = new Group();
        root.getChildren().add(camera);
        root.getChildren().add(earth);
        // root.getChildren().add(sun);
        // root.getChildren().add(ambient);

        RotateTransition rt = new RotateTransition(Duration.seconds(24), earth);
        rt.setByAngle(360);
        rt.setInterpolator(Interpolator.LINEAR);
        rt.setCycleCount(Animation.INDEFINITE);
        rt.play();

        SubScene subScene = new SubScene(root, 400, 300, true, SceneAntialiasing.BALANCED);
        subScene.widthProperty().bind(widthProperty());
        subScene.heightProperty().bind(heightProperty());
        subScene.setFill(Color.TRANSPARENT);
        subScene.setCamera(camera);

        getChildren().add(subScene);
    }
}
