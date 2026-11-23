package be.twofold.valen.middleware.wwise.wem;

import wtf.reversed.toolbox.io.*;

import java.io.*;

public record AkVorbisLoopInfo(
    int loopStartPacketOffset,
    int loopEndPacketOffset,
    short loopBeginExtra,
    short loopEndExtra
) {
    public static AkVorbisLoopInfo read(BinaryReader reader) throws IOException {
        int loopStartPacketOffset = reader.readInt();
        int loopEndPacketOffset = reader.readInt();
        short loopBeginExtra = reader.readShort();
        short loopEndExtra = reader.readShort();

        return new AkVorbisLoopInfo(
            loopStartPacketOffset,
            loopEndPacketOffset,
            loopBeginExtra,
            loopEndExtra
        );
    }
}
