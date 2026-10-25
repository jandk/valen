package be.twofold.valen.middleware.wwise.wem;

import wtf.reversed.toolbox.io.*;

import java.io.*;

public record AkVorbisLoopInfo(
    int loopStartPacketOffset,
    int loopEndPacketOffset,
    short loopBeginExtra,
    short loopEndExtra
) {
    public static AkVorbisLoopInfo read(BinarySource source) throws IOException {
        int loopStartPacketOffset = source.readInt();
        int loopEndPacketOffset = source.readInt();
        short loopBeginExtra = source.readShort();
        short loopEndExtra = source.readShort();

        return new AkVorbisLoopInfo(
            loopStartPacketOffset,
            loopEndPacketOffset,
            loopBeginExtra,
            loopEndExtra
        );
    }
}
