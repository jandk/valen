package be.twofold.valen.game.darkages.reader.anim;

import be.twofold.valen.core.io.*;

import java.io.*;

public record Md6AnimMap(
    int[] constR,
    int[] constS,
    int[] constT,
    int[] constU,
    int[] animR,
    int[] animS,
    int[] animT,
    int[] animU
) {
    public static Md6AnimMap read(DataSource source) throws IOException {
        var position = source.position();
        source.expectByte((byte) 0); // padding
        var offsets = Md6AnimMapOffsets.read(source);

        var constR = source.position(position + offsets.constRRLEOffset()).readObject(Md6AnimMap::decodeRLE);
        var constS = source.position(position + offsets.constSRLEOffset()).readObject(Md6AnimMap::decodeRLE);
        var constT = source.position(position + offsets.constTRLEOffset()).readObject(Md6AnimMap::decodeRLE);
        var constU = source.position(position + offsets.constURLEOffset()).readObject(Md6AnimMap::decodeRLE);

        var animR = source.position(position + offsets.animRRLEOffset()).readObject(Md6AnimMap::decodeRLE);
        var animS = source.position(position + offsets.animSRLEOffset()).readObject(Md6AnimMap::decodeRLE);
        var animT = source.position(position + offsets.animTRLEOffset()).readObject(Md6AnimMap::decodeRLE);
        var animU = source.position(position + offsets.animURLEOffset()).readObject(Md6AnimMap::decodeRLE);

        return new Md6AnimMap(constR, constS, constT, constU, animR, animS, animT, animU);
    }

    static int[] decodeRLE(DataSource source) throws IOException {
        var size = source.readShort();
        if (size < 0) {
            return decodeRLE16(source, size & 0x7FFF);
        } else {
            return decodeRLE08(source, size);
        }
    }

    private static int[] decodeRLE08(DataSource source, int size) throws IOException {
        var result = new int[size];
        for (var o = 0; o < size; ) {
            int count = source.readByte();
            if ((count & 0x80) != 0) {
                throw new UnsupportedOperationException();
            }

            int value = Byte.toUnsignedInt(source.readByte());
            for (var i = 0; i < count; i++) {
                result[o++] = value + i;
            }
        }
        return result;
    }

    private static int[] decodeRLE16(DataSource source, int size) throws IOException {
        var result = new int[size];
        for (var o = 0; o < size; ) {
            int count = source.readByte();
            if ((count & 0x80) != 0) {
                throw new UnsupportedOperationException();
            }

            int value = Short.toUnsignedInt(source.readShort());
            for (var i = 0; i < count; i++) {
                result[o++] = value + i;
            }
        }
        return result;
    }
}
