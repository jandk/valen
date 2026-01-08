package be.twofold.valen.game.source.readers.vtf;

import wtf.reversed.toolbox.io.*;
import wtf.reversed.toolbox.math.*;

import java.io.*;

public record VtfHeader(
    int majorVersion,
    int minorVersion,
    int headerSize,
    short width,
    short height,
    int flags,
    short frames,
    short firstFrame,
    Vector3 reflectivity,
    float bumpmapScale,
    VtfImageFormat highResImageFormat,
    byte mipmapCount,
    VtfImageFormat lowResImageFormat,
    byte lowResImageWidth,
    byte lowResImageHeight,
    short depth,
    int numResources
) {
    public static VtfHeader read(BinarySource source) throws IOException {
        source.expectInt(0x00465456);
        int majorVersion = source.readInt();
        int minorVersion = source.readInt();
        int headerSize = source.readInt();
        short width = source.readShort();
        short height = source.readShort();
        int flags = source.readInt();
        short frames = source.readShort();
        short firstFrame = source.readShort();
        source.skip(4);
        Vector3 reflectivity = Vector3.read(source);
        source.skip(4);
        float bumpmapScale = source.readFloat();
        VtfImageFormat highResImageFormat = VtfImageFormat.fromValue(source.readInt());
        byte mipmapCount = source.readByte();
        VtfImageFormat lowResImageFormat = VtfImageFormat.fromValue(source.readInt());
        byte lowResImageWidth = source.readByte();
        byte lowResImageHeight = source.readByte();

        short depth = 1;
        if (minorVersion >= 2) {
            depth = source.readShort();
        }

        int numResources = 0;
        if (minorVersion >= 3) {
            source.skip(3);
            numResources = source.readInt();
            source.skip(8);
        }

        return new VtfHeader(
            majorVersion,
            minorVersion,
            headerSize,
            width,
            height,
            flags,
            frames,
            firstFrame,
            reflectivity,
            bumpmapScale,
            highResImageFormat,
            mipmapCount,
            lowResImageFormat,
            lowResImageWidth,
            lowResImageHeight,
            depth,
            numResources
        );
    }
}
