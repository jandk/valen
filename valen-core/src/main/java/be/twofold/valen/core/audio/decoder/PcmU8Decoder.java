package be.twofold.valen.core.audio.decoder;

import be.twofold.valen.core.audio.*;
import wtf.reversed.toolbox.collect.*;

import java.io.*;

public final class PcmU8Decoder implements AudioDecoder {
    @Override
    public AudioCodec source() {
        return AudioCodec.PCM_U8;
    }

    @Override
    public Audio decode(Audio audio) throws IOException {
        var src = audio.data();
        var dst = Bytes.allocate(src.length() * Short.BYTES);

        for (int i = 0; i < src.length(); i++) {
            dst.setShort(i * Short.BYTES, (short) ((src.getUnsigned(i) - 128) << 8));
        }
        return new Audio(
            AudioCodec.PCM_S16_LE,
            audio.sampleRate(),
            audio.channels(),
            audio.frameCount(),
            dst
        );
    }
}
