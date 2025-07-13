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
    public static Md6AnimMap read(BinaryReader reader) throws IOException {
        var position = reader.position();
        reader.expectByte((byte) 0); // padding
        var offsets = Md6AnimMapOffsets.read(reader);

        var constR = reader.position(position + offsets.constRRLEOffset()).readObject(Md6AnimMap::decodeRLE);
        var constS = reader.position(position + offsets.constSRLEOffset()).readObject(Md6AnimMap::decodeRLE);
        var constT = reader.position(position + offsets.constTRLEOffset()).readObject(Md6AnimMap::decodeRLE);
        var constU = reader.position(position + offsets.constURLEOffset()).readObject(Md6AnimMap::decodeRLE);

        var animR = reader.position(position + offsets.animRRLEOffset()).readObject(Md6AnimMap::decodeRLE);
        var animS = reader.position(position + offsets.animSRLEOffset()).readObject(Md6AnimMap::decodeRLE);
        var animT = reader.position(position + offsets.animTRLEOffset()).readObject(Md6AnimMap::decodeRLE);
        var animU = reader.position(position + offsets.animURLEOffset()).readObject(Md6AnimMap::decodeRLE);

        return new Md6AnimMap(constR, constS, constT, constU, animR, animS, animT, animU);
    }

    static int[] decodeRLE(BinaryReader reader) throws IOException {
        var size = reader.readShort();
        if (size < 0) {
            return decodeRLE16(reader, size & 0x7FFF);
        } else {
            return decodeRLE08(reader, size);
        }
    }

    private static int[] decodeRLE08(BinaryReader reader, int size) throws IOException {
        var result = new int[size];
        for (var o = 0; o < size; ) {
            int count = reader.readByte();
            if ((count & 0x80) != 0) {
                throw new UnsupportedOperationException();
            }

            int value = Byte.toUnsignedInt(reader.readByte());
            for (var i = 0; i < count; i++) {
                result[o++] = value + i;
            }
        }
        return result;
    }

    private static int[] decodeRLE16(BinaryReader reader, int size) throws IOException {
        var result = new int[size];
        for (var o = 0; o < size; ) {
            int count = reader.readByte();
            if ((count & 0x80) != 0) {
                throw new UnsupportedOperationException();
            }

            int value = Short.toUnsignedInt(reader.readShort());
            for (var i = 0; i < count; i++) {
                result[o++] = value + i;
            }
        }
        return result;
    }
}
