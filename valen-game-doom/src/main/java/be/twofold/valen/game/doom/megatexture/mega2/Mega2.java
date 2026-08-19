package be.twofold.valen.game.doom.megatexture.mega2;

import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.util.*;

public record Mega2(
    Mega2Header header,
    List<Mega2Level> levels,
    Ints offsets,
    List<Mega2Pointer> pointers
) {
    public static Mega2 read(BinarySource source) throws IOException {
        var header = Mega2Header.read(source);
        var levels = source.readObjects(header.quadtreeLevelCount(), Mega2Level::read);

        source.position(header.quadtreeOffset());
        var offsets = source.readInts(header.quadtreeCount());

        source.position(header.pointerOffset());
        var pointers = source.readObjects(header.pointerCount(), Mega2Pointer::read);

        return new Mega2(
            header,
            levels,
            offsets,
            pointers
        );
    }

    public Optional<Mega2Pointer> findPage(int level, int x, int y) {
        var mega2Level = levels.get(level);
        var offset = offsets.get(mega2Level.quadtreeIndex() + y * mega2Level.xBlockCount() + x);
        return offset < 0 ? Optional.empty() : Optional.of(pointers.get(offset));
    }
}
