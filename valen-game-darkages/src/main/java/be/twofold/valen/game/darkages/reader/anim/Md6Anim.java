package be.twofold.valen.game.darkages.reader.anim;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.math.*;

import java.io.*;
import java.util.*;

public record Md6Anim(
    Md6AnimHeader header,
    Md6AnimData data,
    Md6AnimMap map,
    List<Quaternion> constR,
    List<Vector3> constS,
    List<Vector3> constT,
    byte[] frameSetTable,
    int[] frameSetOffsetTable,
    Md6AnimStreamInfo streamInfo
) {
    public static Md6Anim read(DataSource source) throws IOException {
        var header = Md6AnimHeader.read(source);
        var start = source.position();
        var animData = Md6AnimData.read(source);
        var animMap = Md6AnimMap.read(source);

        var constR = source.position(start + animData.constROffset()).readObjects(animMap.constR().length, Md6Anim::decodeQuat);
        var constS = source.position(start + animData.constSOffset()).readObjects(animMap.constS().length, Vector3::read);
        var constT = source.position(start + animData.constTOffset()).readObjects(animMap.constT().length, Vector3::read);

        // TODO: Figure out what index 1 does
        var frameSetTable0 = source.position(start + animData.frameSetTblOffset()[0]).readBytes(animData.numFrames());
        var frameSetTable1 = source.position(start + animData.frameSetTblOffset()[1]).readBytes(animData.numFrames());
        var frameSetOffsetTable = source.position(start + animData.frameSetOffsetTblOffset())
            .readInts(animData.numFrameSets() - animData.numStreamedFrameSets() + 1);

        var streamInfo = source.position(start + header.size()).readObject(Md6AnimStreamInfo::read);
        source.expectEnd(); // doesn't mean much here, but still
        source.position(start); // Move here for frameSet reading

        return new Md6Anim(
            header,
            animData,
            animMap,
            constR,
            constS,
            constT,
            frameSetTable0,
            frameSetOffsetTable,
            streamInfo
        );
    }

    static Quaternion decodeQuat(DataSource source) throws IOException {
        var x = source.readShort();
        var y = source.readShort();
        var z = source.readShort();

        var xBit = (x >>> 15) & 1;
        var yBit = (y >>> 15) & 1;
        var index = (yBit << 1 | xBit);

        var a = Math.fma(x & 0x7FFF, MathF.SQRT_2 / 0x7FFF, -MathF.SQRT1_2);
        var b = Math.fma(y & 0x7FFF, MathF.SQRT_2 / 0x7FFF, -MathF.SQRT1_2);
        var c = Math.fma(z & 0x7FFF, MathF.SQRT_2 / 0x7FFF, -MathF.SQRT1_2);
        var d = MathF.sqrt(1 - a * a - b * b - c * c);

        return switch (index) {
            case 0 -> new Quaternion(a, b, c, d);
            case 1 -> new Quaternion(b, c, d, a);
            case 2 -> new Quaternion(c, d, a, b);
            case 3 -> new Quaternion(d, a, b, c);
            default -> throw new UnsupportedOperationException();
        };
    }
}
