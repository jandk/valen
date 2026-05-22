package be.twofold.valen.game.doom.mega2;

import wtf.reversed.toolbox.io.*;
import wtf.reversed.toolbox.util.*;

import java.io.*;
import java.nio.file.*;

public final class Mega2File {
    private final BinarySource source;
    private final Mega2 mega;

    private Mega2File(BinarySource source, Mega2 mega) {
        this.source = Check.nonNull(source, "source");
        this.mega = Check.nonNull(mega, "mega2");
    }

    public static Mega2File open(Path path) throws IOException {
        BinarySource source = BinarySource.open(path);
        var mega2 = Mega2.read(source);
        return new Mega2File(source, mega2);
    }

    public Mega2 mega() {
        return mega;
    }

    public BinarySource forTile(int level, int x, int y) {
        var megaLevel = mega.levels().get(level);
        var offset = mega.offsets().get(megaLevel.treeIndex() + y * megaLevel.blockXCount() + x);
        if (offset < 0) {
            return null;
        }

        var pointer = mega.pointers().get(offset);
        return source.slice(pointer.offset(), pointer.length());
    }
}
