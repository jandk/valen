package be.twofold.valen.game.darkages.reader.anim;

import wtf.reversed.toolbox.io.*;

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
    public static Md6AnimMap read(BinarySource source) throws IOException {
        var position = source.position();
        source.expectByte((byte) 0); // padding
        var offsets = Md6AnimMapOffsets.read(source);

        var constR = Md6AnimMap.decodeRLE(source.position(position + offsets.constRRLEOffset()));
        var constS = Md6AnimMap.decodeRLE(source.position(position + offsets.constSRLEOffset()));
        var constT = Md6AnimMap.decodeRLE(source.position(position + offsets.constTRLEOffset()));
        var constU = Md6AnimMap.decodeRLE(source.position(position + offsets.constURLEOffset()));

        var animR = Md6AnimMap.decodeRLE(source.position(position + offsets.animRRLEOffset()));
        var animS = Md6AnimMap.decodeRLE(source.position(position + offsets.animSRLEOffset()));
        var animT = Md6AnimMap.decodeRLE(source.position(position + offsets.animTRLEOffset()));
        var animU = Md6AnimMap.decodeRLE(source.position(position + offsets.animURLEOffset()));

        return new Md6AnimMap(constR, constS, constT, constU, animR, animS, animT, animU);
    }

    static int[] decodeRLE(BinarySource source) throws IOException {
        var size = source.readShort();
        if (size < 0) {
            return decodeRLE16(source, size & 0x7FFF);
        } else {
            return decodeRLE08(source, size);
        }
    }

    private static int[] decodeRLE08(BinarySource source, int size) throws IOException {
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

    private static int[] decodeRLE16(BinarySource source, int size) throws IOException {
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
