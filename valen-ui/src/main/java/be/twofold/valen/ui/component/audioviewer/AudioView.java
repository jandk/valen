package be.twofold.valen.ui.component.audioviewer;

import be.twofold.valen.ui.common.*;

public interface AudioView extends View<AudioView.Listener> {
    void setWaveform(Waveform waveform);

    void setStatus(String status);

    void setMessage(String message);

    void setDuration(double duration);

    void setPosition(double position);

    void setPlaying(boolean playing);

    void setPlayable(boolean playable);

    interface Listener extends View.Listener {

        void onPlayPause();

        void onSeek(double position);

        void onTick();

    }
}
