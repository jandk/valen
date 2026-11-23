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

    public RiffChunk.Container read(BinaryReader reader) throws IOException {
        RiffChunk chunk = readChunk(reader, reader.position() + reader.remaining());
        if (!(chunk instanceof RiffChunk.Container container)) {
            throw new IOException("Expected container chunk as the root");
        }
        return container;
    }

    private RiffChunk readChunk(BinaryReader reader, long limit) throws IOException {
        var id = FourCC.read(reader);
        var size = reader.readUnsignedInt();
        var start = reader.position();
        var end = Math.min(start + size, limit);

        RiffChunk chunk;
        if (containers.contains(id)) {
            var type = FourCC.read(reader);
            var subChunks = new ArrayList<RiffChunk>();
            while (end - reader.position() >= 8) {
                subChunks.add(readChunk(reader, end));
            }
            chunk = new RiffChunk.Container(id, type, subChunks);
        } else {
            chunk = new RiffChunk.Data(id, reader.readBytes(Math.toIntExact(end - start)), size);
        }

        reader.position(Math.min(end + (size & 1), limit));
        return chunk;
    }
}
