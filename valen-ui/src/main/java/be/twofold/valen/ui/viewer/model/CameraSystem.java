package be.twofold.valen.ui.viewer.model;

import javafx.scene.*;
import javafx.scene.input.*;
import javafx.scene.transform.*;

final class CameraSystem {
    private final PerspectiveCamera camera = new PerspectiveCamera(true);
    private final Rotate rotateX = new Rotate(180, Rotate.X_AXIS);
    private final Rotate rotateY = new Rotate(0, Rotate.Y_AXIS);
    private final Translate translate = new Translate(0, 0, -200);
    private double mousePosX;
    private double mousePosY;

    public CameraSystem(SubScene subScene) {
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

    private void handleMouseEvent(MouseEvent event) {
        double mouseOldX;
        double mouseOldY;
        if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
            mousePosX = event.getSceneX();
            mousePosY = event.getSceneY();
        } else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED) {
            mouseOldX = mousePosX;
            mouseOldY = mousePosY;
            mousePosX = event.getSceneX();
            mousePosY = event.getSceneY();
            double mouseDeltaX = (mousePosX - mouseOldX);
            double mouseDeltaY = (mousePosY - mouseOldY);

            if ((event.isMiddleButtonDown() || (event.isPrimaryButtonDown() && event.isSecondaryButtonDown()))) {
                camera.setTranslateX(camera.getTranslateX() - mouseDeltaX * 0.1);
                camera.setTranslateY(camera.getTranslateY() + mouseDeltaY * 0.1);
            } else if (event.isPrimaryButtonDown()) {
                rotateX.setAngle(Math.clamp(rotateX.getAngle() - mouseDeltaY * 0.3 * 2.0, 95, 265));
                rotateY.setAngle(rotateY.getAngle() - mouseDeltaX * 0.3 * 2.0);
            } else if (event.isSecondaryButtonDown()) {
                double z = translate.getZ();
                z += (mouseDeltaX + mouseDeltaY) * 0.3;
                translate.setZ(Math.clamp(z, -5000, -200));
            }
        }
    }

    private void handleScrollEvent(ScrollEvent event) {
        if (event.getTouchCount() > 0) {
            camera.setTranslateX(camera.getTranslateX() - (0.01 * event.getDeltaX()));
            camera.setTranslateY(camera.getTranslateY() + (0.01 * event.getDeltaY()));
        } else {
            double z = translate.getZ() - (event.getDeltaY() * 0.2);
            translate.setZ(Math.clamp(z, -5000, -200));
        }
    }

    private void handleZoomEvent(ZoomEvent event) {
        if (!Double.isNaN(event.getZoomFactor()) && event.getZoomFactor() > 0.8 && event.getZoomFactor() < 1.2) {
            double z = translate.getZ() / event.getZoomFactor();
            translate.setZ(Math.clamp(z, -5000, -200));
        }
    }
}
