package be.twofold.valen.ui;

import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;

final class ZoomableScrollPane extends ScrollPane {
    private double scaleValue = 1;
    private final Node target;
    private final Node zoomNode;

    public ZoomableScrollPane(Node target) {
        this.target = target;
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
    }

    private void onScroll(double delta, Point2D point) {
        double zoomFactor = Math.exp(delta * 0.1);

        Bounds innerBounds = zoomNode.getLayoutBounds();
        Bounds viewportBounds = getViewportBounds();

        // calculate pixel offsets from [0, 1] range
        double valX = this.getHvalue() * (innerBounds.getWidth() - viewportBounds.getWidth());
        double valY = this.getVvalue() * (innerBounds.getHeight() - viewportBounds.getHeight());

        scaleValue = scaleValue * zoomFactor;
        target.setScaleX(scaleValue);
        target.setScaleY(scaleValue);
        this.layout(); // refresh ScrollPane scroll positions & target bounds

        // convert target coordinates to zoomTarget coordinates
        Point2D posInZoomTarget = target.parentToLocal(zoomNode.parentToLocal(point));

        // calculate adjustment of scroll position (pixels)
        Point2D adjustment = target.getLocalToParentTransform().deltaTransform(posInZoomTarget.multiply(zoomFactor - 1));

        // convert back to [0, 1] range
        // (too large/small values are automatically corrected by ScrollPane)
        Bounds updatedInnerBounds = zoomNode.getBoundsInLocal();
        this.setHvalue((valX + adjustment.getX()) / (updatedInnerBounds.getWidth() - viewportBounds.getWidth()));
        this.setVvalue((valY + adjustment.getY()) / (updatedInnerBounds.getHeight() - viewportBounds.getHeight()));
    }
}
