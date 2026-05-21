package be.twofold.valen.core.texture.shader;

import be.twofold.valen.core.texture.shader.node.*;
import wtf.reversed.toolbox.util.*;

import java.util.*;

public final class Context {
    private final TilePool pool;
    private final Map<ShaderNode, Integer> consumers;
    private final Map<SourceNode, TileUnpacker> unpackers;
    private final Map<ShaderNode, TileBuffer> results = new IdentityHashMap<>();

    final int x;
    final int y;
    final int z;
    final int width;
    final int height;

    Context(
        TilePool pool,
        Map<ShaderNode, Integer> consumers,
        Map<SourceNode, TileUnpacker> unpackers,
        int x, int y, int z, int width, int height
    ) {
        this.pool = Check.nonNull(pool, "pool");
        this.consumers = Map.copyOf(consumers);
        this.unpackers = Map.copyOf(unpackers);
        this.x = x;
        this.y = y;
        this.z = z;
        this.width = width;
        this.height = height;
    }

    public int pixelCount() {
        return width * height;
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
        unpackers.get(source).unpack(this, target);
    }
}
