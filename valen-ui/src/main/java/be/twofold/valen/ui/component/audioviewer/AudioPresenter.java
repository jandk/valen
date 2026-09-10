package be.twofold.valen.ui.component.audioviewer;

import be.twofold.valen.core.audio.*;
import be.twofold.valen.core.game.*;
import be.twofold.valen.ui.common.*;
import be.twofold.valen.ui.component.*;
import jakarta.inject.*;

import javax.sound.sampled.*;
import java.io.*;
import java.util.*;
import java.util.stream.*;

public final class AudioPresenter extends AbstractPresenter<AudioView> implements AudioView.Listener, Viewer {
    private Clip clip;
    private boolean playing;

    @Inject
    public AudioPresenter(AudioView view) {
        super(view);
        view.setListener(this);
    }

    @Override
    public String getName() {
        return "Audio";
    }

    @Override
    public boolean canPreview(AssetType type) {
        return type == AssetType.AUDIO;
    }

    @Override
    public Object decode(Object data) {
        if (!(data instanceof Audio audio)) {
            return null;
        }

        var pcm = AudioDecoder.convert(audio, AudioCodec.PCM_S16_LE);
        if (pcm.isEmpty()) {
            return null;
        }

        var status = describe(audio);
        var waveform = Waveform.of(pcm.get());
        return new AudioPayload(pcm.get(), waveform, status, null);
    }

    @Override
    public void display(Object payload) {
        resetClip();

        if (!(payload instanceof AudioPayload audioPayload)) {
            clear(null);
            return;
        }

        int playbackChannels = channelsFor(audioPayload.pcmAudio().channels());
        var audio = Downmix.downmix(audioPayload.pcmAudio(), playbackChannels);
        var format = new AudioFormat(audio.sampleRate(), 16, playbackChannels, true, false);
        try {
            var newClip = (Clip) AudioSystem.getLine(new DataLine.Info(Clip.class, format));
            newClip.open(new AudioInputStream(audio.data().asInputStream(), format, audio.frameCount()));
            clip = newClip; // if it fails on open, it's still null
        } catch (LineUnavailableException | IOException e) {
            clear("Error: " + e.getMessage());
            getView().setMessage("Error: " + e.getMessage());
            return;
        }

        getView().setWaveform(audioPayload.waveform());
        getView().setStatus(audioPayload.status());
        getView().setMessage(audioPayload.message());
        getView().setDuration(audio.duration());
        getView().setPosition(0);
    }

    private int channelsFor(List<Channel> channels) {
        var allChannels = new ArrayList<>(channels);
        allChannels.remove(Channel.LOW_FREQUENCY);
        return allChannels.size() == 1
            && allChannels.equals(Channel.MONO) ? 1 : 2;
    }

    private void clear(String message) {
        getView().setWaveform(null);
        getView().setStatus(null);
        getView().setMessage(message);
        getView().setDuration(0);
        getView().setPosition(0);
    }

    @Override
    public void onPlayPause() {
        if (clip == null) {
            return;
        }
        if (playing) {
            clip.stop();
            setPlaying(false);
        } else {
            if (atEnd()) {
                clip.setFramePosition(0);
                updatePosition();
            }
            clip.start();
            setPlaying(true);
        }
    }

    @Override
    public void onSeek(double position) {
        if (clip == null) {
            return;
        }
        clip.setFramePosition((int) (clip.getFrameLength() * position + 0.5));
        updatePosition();
    }

    @Override
    public void onTick() {
        if (clip == null) {
            return;
        }
        if (atEnd()) {
            clip.stop();
            clip.setFramePosition(0);
            getView().setPosition(0);
            setPlaying(false);
        } else {
            updatePosition();
        }
    }

    private void updatePosition() {
        int pos = clip.getFramePosition();
        int len = clip.getFrameLength();
        getView().setPosition(len == 0 ? 0 : (double) pos / len);
    }

    private boolean atEnd() {
        return clip.getFramePosition() >= clip.getFrameLength();
    }

    private String describe(Audio audio) {
        var channels = channelNames(audio.channels());

        return audio.sampleRate() + " Hz" + Constants.TS_DASH +
            channels + Constants.TS_DASH +
            audio.codec();
    }

    private String channelNames(List<Channel> channels) {
        if (channels.equals(Channel.MONO)) {
            return "mono";
        } else if (channels.equals(Channel.STEREO)) {
            return "stereo";
        } else {
            return channels.stream()
                .map(Channel::shortName)
                .collect(Collectors.joining(Constants.TS + "+" + Constants.TS));
        }
    }

    private void resetClip() {
        if (clip != null) {
            clip.stop();
            clip.close();
            clip = null;
        }
        setPlaying(false);
    }

    private void setPlaying(boolean playing) {
        this.playing = playing;
        getView().setPlaying(playing);
    }
}
