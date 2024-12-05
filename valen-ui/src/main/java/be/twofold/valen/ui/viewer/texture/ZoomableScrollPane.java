package be.twofold.valen.ui.viewer.texture;

import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;

final class ZoomableScrollPane extends ScrollPane {
    private double scaleValue = 1;
    private final Node target;
    private final Node zoomNode;
    private boolean lockZoomToFit = false;

    public ZoomableScrollPane(Node target) {
        this.target = target;
        this.target.boundsInLocalProperty().addListener((_, _, newValue) -> zoomToFit(newValue));
        viewportBoundsProperty().addListener((_, _, _) -> zoomToFit(this.target.getBoundsInLocal()));

        this.zoomNode = new Group(target);

        VBox outer = new VBox(zoomNode);
        outer.setAlignment(Pos.CENTER);
        outer.setOnScroll(e -> {
            e.consume();
            onScroll(e.getTextDeltaY(), new Point2D(e.getX(), e.getY()));
        });
        setContent(outer);

        setFitToHeight(true); //center
        setFitToWidth(true); //center
        setPannable(true);
        setHbarPolicy(ScrollBarPolicy.NEVER);
        setVbarPolicy(ScrollBarPolicy.NEVER);

        updateBounds();
    }

    void lockZoomToFit() {
        lockZoomToFit = true;
        zoomToFit(target.getBoundsInLocal());
    }

    void setScaleValue(double scaleValue) {
        this.scaleValue = scaleValue;
        target.setScaleX(this.scaleValue);
        target.setScaleY(this.scaleValue);
    }

    private void zoomToFit(Bounds targetBounds) {
        if (!lockZoomToFit) {
            return;
        }

        var viewportBounds = getViewportBounds();

        double scaleX = viewportBounds.getWidth() / targetBounds.getWidth();
        double scaleY = viewportBounds.getHeight() / targetBounds.getHeight();
        scaleValue = Math.min(scaleX, scaleY);

        target.setScaleX(scaleValue);
        target.setScaleY(scaleValue);
    }

    private void onScroll(double delta, Point2D point) {
        lockZoomToFit = false;
        double zoomFactor = Math.pow(2, delta * 0.1);

        Bounds innerBounds = zoomNode.getLayoutBounds();
        Bounds viewportBounds = getViewportBounds();

        // calculate pixel offsets from [0, 1] range
        double valX = getHvalue() * (innerBounds.getWidth() - viewportBounds.getWidth());
        double valY = getVvalue() * (innerBounds.getHeight() - viewportBounds.getHeight());

        scaleValue = scaleValue * zoomFactor;
        target.setScaleX(scaleValue);
        target.setScaleY(scaleValue);
        layout(); // refresh ScrollPane scroll positions & target bounds

        // convert target coordinates to zoomTarget coordinates
        Point2D posInZoomTarget = target.parentToLocal(zoomNode.parentToLocal(point));

        // calculate adjustment of scroll position (pixels)
        Point2D adjustment = target.getLocalToParentTransform().deltaTransform(posInZoomTarget.multiply(zoomFactor - 1));

        // convert back to [0, 1] range
        // (too large/small values are automatically corrected by ScrollPane)
        Bounds updatedInnerBounds = zoomNode.getBoundsInLocal();
        setHvalue((valX + adjustment.getX()) / (updatedInnerBounds.getWidth() - viewportBounds.getWidth()));
        setVvalue((valY + adjustment.getY()) / (updatedInnerBounds.getHeight() - viewportBounds.getHeight()));
    }
}
