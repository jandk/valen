package be.twofold.valen.game.source.readers.vtf;

import wtf.reversed.toolbox.io.*;
import wtf.reversed.toolbox.math.*;

import java.io.*;
import java.util.*;

public record VtfHeader(
    int majorVersion,
    int minorVersion,
    int headerSize,
    short width,
    short height,
    Set<VtfFlags> flags,
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
        var majorVersion = source.readInt();
        var minorVersion = source.readInt();
        var headerSize = source.readInt();
        var width = source.readShort();
        var height = source.readShort();
        var flags = VtfFlags.fromValue(source.readInt());
        var frames = source.readShort();
        var firstFrame = source.readShort();
        source.expectZeros(4);
        var reflectivity = Vector3.read(source);
        source.expectZeros(4);
        var bumpmapScale = source.readFloat();
        var highResImageFormat = VtfImageFormat.read(source);
        var mipmapCount = source.readByte();
        var lowResImageFormat = VtfImageFormat.read(source);
        var lowResImageWidth = source.readByte();
        var lowResImageHeight = source.readByte();

        short depth = 1;
        if (minorVersion >= 2) {
            depth = source.readShort();
        }

        var numResources = 0;
        if (minorVersion >= 3) {
            source.expectZeros(3);
            numResources = source.readInt();
            source.expectZeros(8);
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
