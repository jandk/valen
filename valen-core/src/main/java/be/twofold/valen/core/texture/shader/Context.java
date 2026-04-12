package be.twofold.valen.core.texture.shader;

import be.twofold.valen.core.texture.shader.node.*;
import wtf.reversed.toolbox.util.*;

import java.util.*;

public final class Context {
    private final TilePool pool;
    private final Map<ShaderNode, Integer> consumers;
    private final Map<SourceNode, TileUnpacker> unpackers;
    private final Map<ShaderNode, TileBuffer> results = new IdentityHashMap<>();

    private final int srcX;
    private final int srcY;
    private final int srcZ;
    private final int tileW;
    private final int tileH;

    Context(
        TilePool pool,
        Map<ShaderNode, Integer> consumers,
        Map<SourceNode, TileUnpacker> unpackers,
        int srcX, int srcY, int srcZ, int tileW, int tileH
    ) {
        this.pool = Check.nonNull(pool, "pool");
        this.consumers = Map.copyOf(consumers);
        this.unpackers = Map.copyOf(unpackers);
        this.srcX = srcX;
        this.srcY = srcY;
        this.srcZ = srcZ;
        this.tileW = tileW;
        this.tileH = tileH;
    }

    public int pixelCount() {
        return tileW * tileH;
    }

    public TileBuffer allocate(ShaderNode node) {
        var buf = new TileBuffer(pool, consumers.get(node));
        results.put(node, buf);
        return buf;
    }

    public TileBuffer steal(ShaderNode from, ShaderNode to) {
        var buf = results.get(from);
        buf.resetRefCount(consumers.get(to));
        results.put(to, buf);
        return buf;
    }

    public TileBuffer get(ShaderNode node) {
        return results.get(node);
    }

    public void unpack(SourceNode source, float[] target) {
        unpackers.get(source).unpack(srcX, srcY, srcZ, target, tileW, tileH);
    }
}
