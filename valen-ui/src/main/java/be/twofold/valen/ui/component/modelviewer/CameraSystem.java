package be.twofold.valen.ui.component.modelviewer;

import javafx.scene.*;
import javafx.scene.input.*;
import javafx.scene.transform.*;

final class CameraSystem {
    private final PerspectiveCamera camera = new PerspectiveCamera(true);
    private final Rotate rotateX = new Rotate(0, Rotate.X_AXIS);
    private final Rotate rotateY = new Rotate(0, Rotate.Y_AXIS);
    private final Translate translate = new Translate(0, 0, -200);
    private double mousePosX;
    private double mousePosY;

    public CameraSystem(SubScene subScene) {
        camera.setFieldOfView(60);
        camera.setNearClip(1.0);
        camera.setFarClip(10000.0);
        camera.getTransforms().addAll(
            rotateY,
            rotateX,
            translate
        );

        subScene.setCamera(camera);
        subScene.addEventHandler(MouseEvent.ANY, this::handleMouseEvent);
        subScene.addEventHandler(ScrollEvent.ANY, this::handleScrollEvent);
        subScene.addEventHandler(ZoomEvent.ANY, this::handleZoomEvent);
    }

    public Camera camera() {
        return camera;
    }

    public void reset() {
        rotateX.setAngle(0);
        rotateY.setAngle(0);
        translate.setX(0);
        translate.setY(0);
        translate.setZ(-200);
    }

    private void handleMouseEvent(MouseEvent event) {
        if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
            mousePosX = event.getSceneX();
            mousePosY = event.getSceneY();
        } else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED) {
            double mouseOldX = mousePosX;
            double mouseOldY = mousePosY;
            mousePosX = event.getSceneX();
            mousePosY = event.getSceneY();
            double mouseDeltaX = (mousePosX - mouseOldX);
            double mouseDeltaY = (mousePosY - mouseOldY);

            if (event.isPrimaryButtonDown()) {
                rotateX.setAngle(Math.clamp(rotateX.getAngle() - mouseDeltaY * 0.5, -85, +85));
                rotateY.setAngle(rotateY.getAngle() + mouseDeltaX * 0.5);
            } else if (event.isSecondaryButtonDown()) {
                translate.setX(translate.getX() - mouseDeltaX * 0.1);
                translate.setY(translate.getY() - mouseDeltaY * 0.1);
            } else if (event.isMiddleButtonDown()) {
                double z = translate.getZ();
                z += (mouseDeltaX + mouseDeltaY) * 0.1;
                translate.setZ(Math.min(z, 0));
            }
        }
    }

    private void handleScrollEvent(ScrollEvent event) {
        if (event.getTouchCount() > 0) {
            camera.setTranslateX(camera.getTranslateX() - (0.01 * event.getDeltaX()));
            camera.setTranslateY(camera.getTranslateY() + (0.01 * event.getDeltaY()));
        } else {
            double z = translate.getZ();
            double factor = Math.max(Math.abs(translate.getZ()), 10);
            z += (event.getDeltaY() * factor * 0.002);
            translate.setZ(Math.min(z, 0));
        }
    }

    private void handleZoomEvent(ZoomEvent event) {
        if (!Double.isNaN(event.getZoomFactor()) && event.getZoomFactor() > 0.8 && event.getZoomFactor() < 1.2) {
            double z = translate.getZ() / event.getZoomFactor();
            translate.setZ(Math.min(z, 0));
        }
    }
}