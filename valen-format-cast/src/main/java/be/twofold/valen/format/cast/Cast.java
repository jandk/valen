package be.twofold.valen.format.cast;

import be.twofold.valen.format.cast.io.*;
import be.twofold.valen.format.cast.node.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.*;

public final class Cast extends AbstractList<CastNode> {
    private final List<CastNode> rootNodes;
    private final AtomicLong hasher = new AtomicLong(0x5A4C524E454C4156L);

    public Cast() {
        this(new ArrayList<>());
    }

    private Cast(List<CastNode> rootNodes) {
        this.rootNodes = Objects.requireNonNull(rootNodes);
    }

    public static Cast read(InputStream in) throws IOException {
        try (var reader = new BinaryReader(new BufferedInputStream(in))) {
            return read(reader);
        }
    }

    public static Cast read(BinaryReader reader) throws IOException {
        int magic = reader.readInt();
        if (magic != 0x74736163) {
            throw new IOException("Invalid magic number");
        }

        int version = reader.readInt();
        if (version != 1) {
            throw new IOException("Invalid version");
        }

        int rootNodeCount = reader.readInt();
        int flags = reader.readInt();
        if (flags != 0) {
            throw new IOException("Invalid flags");
        }

        var rootNodes = new ArrayList<CastNode>(rootNodeCount);
        for (int i = 0; i < rootNodeCount; i++) {
            rootNodes.add(CastNode.read(reader));
        }
        return new Cast(rootNodes);
    }

    public Nodes.RootNode createRoot() {
        return new Nodes.RootNode(hasher);
    }

    @Override
    public int size() {
        return rootNodes.size();
    }

    @Override
    public CastNode get(int index) {
        return rootNodes.get(index);
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof Cast cast
            && rootNodes.equals(cast.rootNodes);
    }

    @Override
    public int hashCode() {
        return rootNodes.hashCode();
    }

    @Override
    public String toString() {
        return "Cast(" + rootNodes + ")";
    }
}
