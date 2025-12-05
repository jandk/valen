package be.twofold.valen.game.darkages.reader.anim;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.math.*;
import be.twofold.valen.core.util.collect.*;

import java.io.*;
import java.util.*;

public record Md6Anim(
    Md6AnimHeader header,
    Md6AnimData data,
    Md6AnimMap map,
    List<Quaternion> constR,
    List<Vector3> constS,
    List<Vector3> constT,
    Bytes frameSetTable,
    Ints frameSetOffsetTable,
    Md6AnimStreamInfo streamInfo
) {
    public static Md6Anim read(BinaryReader reader) throws IOException {
        var header = Md6AnimHeader.read(reader);
        var start = reader.position();
        var animData = Md6AnimData.read(reader);
        var animMap = Md6AnimMap.read(reader);

        var constR = reader.position(start + animData.constROffset()).readObjects(animMap.constR().length, Md6Anim::decodeQuat);
        var constS = reader.position(start + animData.constSOffset()).readObjects(animMap.constS().length, Vector3::read);
        var constT = reader.position(start + animData.constTOffset()).readObjects(animMap.constT().length, Vector3::read);

        // TODO: Figure out what index 1 does
        var frameSetTable0 = reader.position(start + animData.frameSetTblOffset().get(0)).readBytes(animData.numFrames());
        // var frameSetTable1 = source.position(start + animData.frameSetTblOffset()[1]).readBytes(animData.numFrames());
        var frameSetOffsetTable = reader.position(start + animData.frameSetOffsetTblOffset()).readInts(animData.numFrameSets() + 1);

        var streamInfo = reader.position(start + header.size()).readObject(Md6AnimStreamInfo::read);
        reader.expectEnd(); // doesn't mean much here, but still
        reader.position(start); // Move here for frameSet reading

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

    static Quaternion decodeQuat(BinaryReader reader) throws IOException {
        var x = reader.readShort();
        var y = reader.readShort();
        var z = reader.readShort();

        var xBit = (x >>> 15) & 1;
        var yBit = (y >>> 15) & 1;
        var index = (yBit << 1 | xBit);

        var a = Math.fma(x & 0x7FFF, MathF.SQRT_2 / 0x8000, -MathF.SQRT1_2);
        var b = Math.fma(y & 0x7FFF, MathF.SQRT_2 / 0x8000, -MathF.SQRT1_2);
        var c = Math.fma(z & 0x7FFF, MathF.SQRT_2 / 0x8000, -MathF.SQRT1_2);
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
