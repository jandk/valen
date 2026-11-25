package be.twofold.valen.ui.component.audioviewer;

import javafx.geometry.*;
import javafx.scene.canvas.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;

import java.util.*;

final class WaveformPane extends Pane {
    // TODO: I have to theme using CSS...
    private static final Color BACKGROUND = Color.rgb(50, 50, 50);
    private static final Color PEAK = Color.rgb(103, 124, 228);
    private static final Color RMS = Color.rgb(131, 140, 243);
    private static final Color MIDLINE = PEAK;
    private static final Color CLIPPED = Color.rgb(239, 71, 111);
    private static final Color SHADE = Color.rgb(50, 50, 50, 0.5);
    private static final Color LABEL = Color.rgb(220, 220, 220);
    private static final Color LABEL_BACKGROUND = Color.rgb(30, 30, 30, 0.8);
    private static final double LABEL_INSET = 5;
    private static final double LABEL_PADDING = 3;

    private static final double LANE_PADDING = 2;
    private static final float CLIP = 0.99f;

    private final Canvas waveCanvas = new Canvas();
    private final Rectangle shade = new Rectangle();
    private final List<Label> labels = new ArrayList<>();

    private Waveform waveform;
    private double progress;

    WaveformPane() {
        shade.setFill(SHADE);
        shade.setManaged(false);
        shade.setMouseTransparent(true);
        getChildren().addAll(waveCanvas, shade);
    }

    void setWaveform(Waveform waveform) {
        this.waveform = waveform;
        setProgress(0.0);
        drawWave();
        updateLabels();
    }

    void setProgress(double progress) {
        this.progress = Math.clamp(progress, 0.0, 1.0);
        updateShade();
    }

    @Override
    protected void layoutChildren() {
        double width = getWidth();
        double height = getHeight();
        waveCanvas.setWidth(width);
        waveCanvas.setHeight(height);
        shade.setHeight(height);
        drawWave();
        layoutLabels();
        updateShade();
    }

    private void drawWave() {
        GraphicsContext gc = waveCanvas.getGraphicsContext2D();
        int width = (int) waveCanvas.getWidth();
        int height = (int) waveCanvas.getHeight();

        gc.setFill(BACKGROUND);
        gc.fillRect(0, 0, width, height);

        if (waveform == null || width <= 0 || height <= 0) {
            return;
        }

        int channels = waveform.channels().size();
        int buckets = waveform.buckets();
        float laneHeight = (float) height / (float) channels;

        int columns = Math.min(width, buckets);
        if (columns <= 0) {
            return;
        }

        for (int channel = 0; channel < channels; channel++) {
            float mid = laneHeight * channel + laneHeight / 2;
            double half = laneHeight / 2 - LANE_PADDING;
            if (half <= 0) {
                continue;
            }

            gc.setFill(MIDLINE);
            gc.fillRect(0, mid - 1, width, 2);

            int offset = channel * buckets;
            for (int i = 0; i < columns; i++) {
                int xMin = (i/**/) * width / columns;
                int xMax = (i + 1) * width / columns;
                int bMin = (i/**/) * buckets / columns;
                int bMax = (i + 1) * buckets / columns;

                float lo = waveform.min()[offset + bMin];
                float hi = waveform.max()[offset + bMin];
                double sum = waveform.meanSquare()[offset + bMin];
                for (int b = bMin + 1; b < bMax; b++) {
                    lo = Math.min(lo, waveform.min()[offset + b]);
                    hi = Math.max(hi, waveform.max()[offset + b]);
                    sum += waveform.meanSquare()[offset + b];
                }

                // JavaFX y always trips me up
                int top = (int) (mid - hi * half + 0.5);
                int bot = (int) (mid - lo * half + 0.5);
                boolean clipping = lo <= -CLIP && hi >= CLIP;

                gc.setFill(clipping ? CLIPPED : PEAK);
                gc.fillRect(xMin, top, xMax - xMin, bot - top);

                if (!clipping) {
                    double rms = Math.sqrt(sum / (bMax - bMin)) * half;
                    int rmsTop = (int) (mid - rms + 0.5);
                    int rmsHeight = (int) (2 * rms + 0.5);
                    gc.setFill(RMS);
                    gc.fillRect(xMin, rmsTop, xMax - xMin, rmsHeight);
                }
            }
        }
    }

    private void updateLabels() {
        getChildren().removeAll(labels);
        labels.clear();
        if (waveform == null) {
            return;
        }

        var background = new Background(new BackgroundFill(LABEL_BACKGROUND, CornerRadii.EMPTY, Insets.EMPTY));
        for (var channel : waveform.channels()) {
            var label = new Label(channel.shortName());
            label.setTextFill(LABEL);
            label.setBackground(background);
            label.setPadding(new Insets(LABEL_PADDING));
            label.setManaged(false);
            label.setMouseTransparent(true);
            labels.add(label);
        }
        getChildren().addAll(labels);
    }

    private void layoutLabels() {
        if (labels.isEmpty()) {
            return;
        }

        double laneHeight = getHeight() / labels.size();
        boolean visible = laneHeight / 2 - LANE_PADDING > 0;
        for (int i = 0; i < labels.size(); i++) {
            var label = labels.get(i);
            label.setVisible(visible);
            label.autosize();
            label.relocate(snapPositionX(LABEL_INSET), snapPositionY(laneHeight * i + LABEL_INSET));
        }
    }

    private void updateShade() {
        double width = getWidth();
        int x = waveform == null ? 0 : (int) (progress * width + 0.5);
        if (false) {
            shade.setX(x);
            shade.setWidth(Math.max(0, width - x));
        } else {
            shade.setX(0);
            shade.setWidth(x);
        }
    }
}
