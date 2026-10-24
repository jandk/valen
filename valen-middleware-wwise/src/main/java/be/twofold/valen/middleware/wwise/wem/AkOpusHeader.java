package be.twofold.valen.middleware.wwise.wem;

import wtf.reversed.toolbox.io.*;

import java.io.*;

public record AkOpusHeader(
    int totalPCMFrames,
    int endGranuleOffset,
    int endGranulePCM
) implements CodecHeader {
    public static AkOpusHeader read(BinarySource source) throws IOException {
        int totalPCMFrames = source.readInt();
        int endGranuleOffset = source.readInt();
        int endGranulePCM = source.readInt();

        return new AkOpusHeader(
            totalPCMFrames,
            endGranuleOffset,
            endGranulePCM
        );
    }
}
