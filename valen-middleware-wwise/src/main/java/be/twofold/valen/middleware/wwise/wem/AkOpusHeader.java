package be.twofold.valen.middleware.wwise.wem;

import wtf.reversed.toolbox.io.*;

import java.io.*;

public record AkOpusHeader(
    int totalPCMFrames,
    int endGranuleOffset,
    int endGranulePCM
) implements CodecHeader {
    public static AkOpusHeader read(BinaryReader reader) throws IOException {
        int totalPCMFrames = reader.readInt();
        int endGranuleOffset = reader.readInt();
        int endGranulePCM = reader.readInt();

        return new AkOpusHeader(
            totalPCMFrames,
            endGranuleOffset,
            endGranulePCM
        );
    }
}
