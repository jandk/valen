package be.twofold.valen.game.qc.reader.pct;

import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;

public record PctTag(
    PctTagType type,
    Bytes data
) {
    public static PctTag read(BinarySource source) throws IOException {
        var position = Math.toIntExact(source.position());

        var type = PctTagType.read(source);
        var next = source.readInt();
        var data = source.readBytes(next - position - 6);

        return new PctTag(type, data);
    }
}
