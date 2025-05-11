package be.twofold.valen.format.cast;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.*;

public final class Cast extends AbstractList<CastNode> {
    private final AtomicLong hasher = new AtomicLong(0x5A4C524E454C4156L);
    final List<CastNode> rootNodes;

    public Cast() {
        this(new ArrayList<>());
    }

    Cast(List<CastNode> rootNodes) {
        this.rootNodes = Objects.requireNonNull(rootNodes);
    }

    public static Cast read(InputStream in) throws IOException {
        return CastReader.read(in);
    }

    public void write(OutputStream out) throws IOException {
        CastWriter.write(this, out);
    }

    public CastNode.Root createRoot() {
        CastNode.Root root = new CastNode.Root(hasher);
        rootNodes.add(root);
        return root;
    }

    @Override
    public int size() {
        return rootNodes.size();
    }

    @Override
    public CastNode get(int index) {
        return rootNodes.get(index);
    }
}
