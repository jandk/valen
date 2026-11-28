package be.twofold.valen.game.idtech.md6anim;

import be.twofold.valen.game.idtech.game.*;
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
    public static Md6AnimMap read(BinaryReader reader, int base, Md6AnimMapOffsets offsets, IdTechGame game) throws IOException {
        var shortHeader = game == IdTechGame.DARK_AGES;

        var constR = Md6AnimDecoder.decodeRle(reader.position(base + offsets.constRRLEOffset()), shortHeader);
        var constS = Md6AnimDecoder.decodeRle(reader.position(base + offsets.constSRLEOffset()), shortHeader);
        var constT = Md6AnimDecoder.decodeRle(reader.position(base + offsets.constTRLEOffset()), shortHeader);
        var constU = Md6AnimDecoder.decodeRle(reader.position(base + offsets.constURLEOffset()), shortHeader);

        var animR = Md6AnimDecoder.decodeRle(reader.position(base + offsets.animRRLEOffset()), shortHeader);
        var animS = Md6AnimDecoder.decodeRle(reader.position(base + offsets.animSRLEOffset()), shortHeader);
        var animT = Md6AnimDecoder.decodeRle(reader.position(base + offsets.animTRLEOffset()), shortHeader);
        var animU = Md6AnimDecoder.decodeRle(reader.position(base + offsets.animURLEOffset()), shortHeader);

        return new Md6AnimMap(
            constR,
            constS,
            constT,
            constU,
            animR,
            animS,
            animT,
            animU
        );
    }
}
