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
    public static AkVorbisHeader read(BinaryReader reader) throws IOException {
        int totalPCMFrames = reader.readInt();
        AkVorbisLoopInfo loopInfo = AkVorbisLoopInfo.read(reader);
        int seekTableSize = reader.readInt();
        int vorbisDataOffset = reader.readInt();
        short maxPacketSize = reader.readShort();
        short lastGranuleExtra = reader.readShort();
        int decodeAllocSize = reader.readInt();
        int decodeX64AllocSize = reader.readInt();
        int hashCodebook = reader.readInt();
        byte blockSize0 = reader.readByte();
        byte blockSize1 = reader.readByte();

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
