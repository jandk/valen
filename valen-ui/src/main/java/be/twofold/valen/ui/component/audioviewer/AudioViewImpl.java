package be.twofold.valen.ui.component.audioviewer;

import be.twofold.valen.ui.common.*;
import jakarta.inject.*;
import javafx.animation.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.shape.*;

public final class AudioViewImpl extends AbstractView<AudioView.Listener> implements AudioView {
    private static final String PLAY = "M 0 0 L 12 6 L 0 12 Z";
    private static final String PAUSE = "M 0 0 H 4 V 12 H 0 Z M 8 0 H 12 V 12 H 8 Z";

    private final VBox root = new VBox();
    private final SVGPath playPauseIcon = new SVGPath();
    private final Button playPauseButton = new Button();
    private final Label timeLabel = new Label();
    private final Label statusLabel = new Label();
    private final Label messageLabel = new Label();
    private final WaveformPane waveformPane = new WaveformPane();
    private final StackPane content = new StackPane();
    private final AnimationTimer ticker = new AnimationTimer() {
        @Override
        public void handle(long now) {
            getListener().onTick();
        }
    };

    private double duration;
    private double position;

    @Inject
    public AudioViewImpl() {
        buildUI();
    }

    @Override
    public Parent getFXNode() {
        return root;
    }

    @Override
    public void setWaveform(Waveform waveform) {
        waveformPane.setWaveform(waveform);
        waveformPane.setVisible(waveform != null);
        waveformPane.setManaged(waveform != null);
    }

    @Override
    public void setStatus(String status) {
        statusLabel.setText(status != null ? status : "");
    }

    @Override
    public void setMessage(String message) {
        messageLabel.setText(message != null ? message : "");
        messageLabel.setVisible(message != null);
        messageLabel.setManaged(message != null);
    }

    @Override
    public void setDuration(double duration) {
        this.duration = duration;
        updateTime();
    }

    @Override
    public void setPosition(double position) {
        this.position = position;
        waveformPane.setProgress(position);
        updateTime();
    }

    @Override
    public void setPlaying(boolean playing) {
        playPauseIcon.setContent(playing ? PAUSE : PLAY);
        if (playing) {
            ticker.start();
        } else {
            ticker.stop();
        }
    }

    private void updateTime() {
        timeLabel.setText(format(position * duration) + Constants.TS_SLASH + format(duration));
    }

    private String format(double seconds) {
        return String.format("%02d:%04.1f", (int) (seconds / 60), seconds % 60);
    }

    private void seek(MouseEvent event) {
        if (duration <= 0 || waveformPane.getWidth() <= 0) {
            return;
        }
        getListener().onSeek(Math.clamp(event.getX() / waveformPane.getWidth(), 0.0, 1.0));
    }

    private void buildUI() {
        root.setPrefSize(900, 600);

        playPauseIcon.getStyleClass().add("transport-icon");
        playPauseButton.setGraphic(playPauseIcon);
        playPauseButton.setOnAction(event -> getListener().onPlayPause());

        var spacer = new Pane();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        var toolBar = new ToolBar(
            playPauseButton,
            new Separator(),
            timeLabel,
            spacer,
            statusLabel
        );

        waveformPane.setOnMousePressed(this::seek);

        content.getChildren().setAll(waveformPane, messageLabel);
        VBox.setVgrow(content, Priority.ALWAYS);

        root.getChildren().setAll(toolBar, content);

        setMessage(null);
        setDuration(0);
        setPosition(0);
    }
}
