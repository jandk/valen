package be.twofold.valen.game.doom.mega2;

import be.twofold.valen.game.doom.megatexture.mega2.*;
import wtf.reversed.toolbox.io.*;
import wtf.reversed.toolbox.util.*;

import java.io.*;

/**
 * A single {@code .mega2} file, holding one rectangle of the virtual texture atlas as pages.
 */
public final class Mega2File {
    private final BinarySource source;
    private final Mega2 mega;

    private Mega2File(BinarySource source, Mega2 mega) {
        this.source = Check.nonNull(source, "source");
        this.mega = Check.nonNull(mega, "mega2");
    }

    public static Mega2File open(BinarySource source) throws IOException {
        var mega2 = Mega2.read(source);
        return new Mega2File(source, mega2);
    }

    public Mega2 mega() {
        return mega;
    }

    public BinarySource findPage(int level, int x, int y) {
        var megaLevel = mega.levels().get(level);
        var offset = mega.offsets().get(megaLevel.quadtreeIndex() + y * megaLevel.xBlockCount() + x);
        if (offset < 0) {
            return null;
        }

        var pointer = mega.pointers().get(offset);
        return source.slice(pointer.offset(), pointer.length());
    }
}
