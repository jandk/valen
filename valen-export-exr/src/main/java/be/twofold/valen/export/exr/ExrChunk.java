package be.twofold.valen.export.exr;

import java.nio.*;

record ExrChunk(
    int yCoordinate,
    int pixelDataSize,
    ByteBuffer pixelData
) {
    public ExrChunk {
        pixelData = pixelData.asReadOnlyBuffer();
    }

    @Override
    public String toString() {
        return "ExrChunk(" +
            "yCoordinate=" + yCoordinate + ", " +
            "pixelDataSize=" + pixelDataSize + ", " +
            "pixelData=[" + pixelData.limit() + " bytes]" +
            ")";
    }
}
