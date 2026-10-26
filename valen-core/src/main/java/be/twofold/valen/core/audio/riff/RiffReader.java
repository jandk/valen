package be.twofold.valen.core.audio.riff;

import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.util.*;

public final class RiffReader {
    private static final Set<FourCC> CONTAINERS = Set.of(
        FourCC.parse("RIFF"),
        FourCC.parse("LIST")
    );

    private final Set<FourCC> containers;

    public RiffReader(FourCC... containers) {
        if (containers == null || containers.length == 0) {
            this.containers = CONTAINERS;
        } else {
            var allContainers = new HashSet<>(CONTAINERS);
            allContainers.addAll(Arrays.asList(containers));
            this.containers = Set.copyOf(allContainers);
        }
    }

    public RiffChunk.Container read(BinarySource source) throws IOException {
        RiffChunk chunk = readChunk(source, source.position() + source.remaining());
        if (!(chunk instanceof RiffChunk.Container container)) {
            throw new IOException("Expected container chunk as the root");
        }
        return container;
    }

    private RiffChunk readChunk(BinarySource source, long limit) throws IOException {
        var id = FourCC.read(source);
        var size = Integer.toUnsignedLong(source.readInt());
        var start = source.position();
        var end = Math.min(start + size, limit);

        RiffChunk chunk;
        if (containers.contains(id)) {
            var type = FourCC.read(source);
            var subChunks = new ArrayList<RiffChunk>();
            while (end - source.position() >= 8) {
                subChunks.add(readChunk(source, end));
            }
            chunk = new RiffChunk.Container(id, type, subChunks);
        } else {
            chunk = new RiffChunk.Data(id, source.readBytes(Math.toIntExact(end - start)), size);
        }

        source.position(Math.min(end + (size & 1), limit));
        return chunk;
    }
}
