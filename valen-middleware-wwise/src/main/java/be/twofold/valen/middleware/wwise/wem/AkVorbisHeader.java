package be.twofold.valen.middleware.wwise.wem;

import wtf.reversed.toolbox.io.*;

import java.io.*;

public record AkVorbisHeader(
    int totalPCMFrames,
    AkVorbisLoopInfo loopInfo,
    int seekTableSize,
    int vorbisDataOffset,
    short maxPacketSize,
    short lastGranuleExtra,
    int decodeAllocSize,
    int decodeX64AllocSize,
    int hashCodebook,
    byte blockSize0,
    byte blockSize1
) implements CodecHeader {
    public static AkVorbisHeader read(BinarySource source) throws IOException {
        int totalPCMFrames = source.readInt();
        AkVorbisLoopInfo loopInfo = AkVorbisLoopInfo.read(source);
        int seekTableSize = source.readInt();
        int vorbisDataOffset = source.readInt();
        short maxPacketSize = source.readShort();
        short lastGranuleExtra = source.readShort();
        int decodeAllocSize = source.readInt();
        int decodeX64AllocSize = source.readInt();
        int hashCodebook = source.readInt();
        byte blockSize0 = source.readByte();
        byte blockSize1 = source.readByte();

        return new AkVorbisHeader(
            totalPCMFrames,
            loopInfo,
            seekTableSize,
            vorbisDataOffset,
            maxPacketSize,
            lastGranuleExtra,
            decodeAllocSize,
            decodeX64AllocSize,
            hashCodebook,
            blockSize0,
            blockSize1
        );
    }
}
