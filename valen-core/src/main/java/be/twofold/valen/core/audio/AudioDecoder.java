package be.twofold.valen.core.audio;

public interface AudioDecoder {

    boolean canDecode(Audio audio, AudioCodec target);

    Audio decode(Audio audio, AudioCodec target);

}
