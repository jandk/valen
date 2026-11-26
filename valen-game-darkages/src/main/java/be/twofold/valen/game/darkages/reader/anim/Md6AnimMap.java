package be.twofold.valen.game.darkages.reader.anim;

import be.twofold.valen.game.idtech.md6anim.*;
import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;

public record Md6AnimMap(
    Ints constR,
    Ints constS,
    Ints constT,
    Ints constU,
    Ints animR,
    Ints animS,
    Ints animT,
    Ints animU
) {
    public static Md6AnimMap read(BinaryReader reader) throws IOException {
        var position = reader.position();
        reader.expectByte((byte) 0); // padding
        var offsets = Md6AnimMapOffsets.read(reader);

        var constR = Md6AnimDecoder.decodeRle(reader.position(position + offsets.constRRLEOffset()), true);
        var constS = Md6AnimDecoder.decodeRle(reader.position(position + offsets.constSRLEOffset()), true);
        var constT = Md6AnimDecoder.decodeRle(reader.position(position + offsets.constTRLEOffset()), true);
        var constU = Md6AnimDecoder.decodeRle(reader.position(position + offsets.constURLEOffset()), true);

        var animR = Md6AnimDecoder.decodeRle(reader.position(position + offsets.animRRLEOffset()), true);
        var animS = Md6AnimDecoder.decodeRle(reader.position(position + offsets.animSRLEOffset()), true);
        var animT = Md6AnimDecoder.decodeRle(reader.position(position + offsets.animTRLEOffset()), true);
        var animU = Md6AnimDecoder.decodeRle(reader.position(position + offsets.animURLEOffset()), true);

        return new Md6AnimMap(constR, constS, constT, constU, animR, animS, animT, animU);
    }
}
