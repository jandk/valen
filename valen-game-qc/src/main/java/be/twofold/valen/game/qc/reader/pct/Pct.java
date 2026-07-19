package be.twofold.valen.game.qc.reader.pct;

import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.util.*;

public record Pct(
    List<PctTag> tags
) {
    public static Pct read(BinarySource source) throws IOException {
        List<PctTag> tags = new ArrayList<>();
        while (source.remaining() > 0) {
            tags.add(PctTag.read(source));
        }

        return new Pct(tags);
    }
}
