package be.twofold.valen.core.texture.shader;

import be.twofold.valen.core.texture.*;
import be.twofold.valen.core.texture.shader.node.*;
import wtf.reversed.toolbox.util.*;

import java.util.*;
import java.util.stream.*;

public final class Shader {
    private static final int TILE_SIZE = 256;
    private static final TilePool POOL = new TilePool(TILE_SIZE);

    private final ShaderNode root;
    private final TextureFormat format;
    private final Map<ShaderNode, Integer> consumerCounts;
    private final List<ShaderNode> executionOrder;

    private Shader(ShaderNode root, TextureFormat format) {
        this.root = Check.nonNull(root, "root");
        this.format = Check.nonNull(format, "format");
        this.consumerCounts = computeCounts(root);
        this.executionOrder = topologicalSort(root);
    }

    // region Graph

    private static Map<ShaderNode, Integer> computeCounts(ShaderNode root) {
        var counts = new IdentityHashMap<ShaderNode, Integer>();
        counts.put(root, 1);
        computeCounts(root, counts);
        return counts;
    }

    private static void computeCounts(ShaderNode node, Map<ShaderNode, Integer> counts) {
        for (var input : node.inputs()) {
            if (counts.merge(input, 1, Integer::sum) == 1) {
                computeCounts(input, counts);
            }
        }
    }

    private static List<ShaderNode> topologicalSort(ShaderNode root) {
        var order = new ArrayList<ShaderNode>();
        var visited = Collections.newSetFromMap(new IdentityHashMap<ShaderNode, Boolean>());
        topologicalSort(root, visited, order);
        return List.copyOf(order);
    }

    private static void topologicalSort(ShaderNode node, Set<ShaderNode> visited, List<ShaderNode> order) {
        if (!visited.add(node)) {
            return;
        }
        for (var input : node.inputs()) {
            topologicalSort(input, visited, order);
        }
        order.add(node);
    }

    // endregion

    public static Shader of(ShaderNode root, TextureFormat format) {
        return new Shader(root, format);
    }

    public Surface execute(SourceNode.Bind... binds) {
        Check.argument(binds.length > 0, "At least one binding is required");
        var first = binds[0].surface();
        for (int i = 1; i < binds.length; i++) {
            var s = binds[i].surface();
            Check.argument(
                first.width() == s.width() && first.height() == s.height() &&
                    first.depth() == s.depth() && first.format() == s.format(),
                "All surfaces must be the same size and format");
        }

        var unpackers = Arrays.stream(binds).collect(Collectors.toMap(
            SourceNode.Bind::source,
            bind -> TileUnpacker.forSurface(bind.surface())
        ));

        var target = Surface.create(first.width(), first.height(), first.depth(), format);
        var packer = TilePacker.forSurface(target);
        process(first.width(), first.height(), first.depth(), unpackers, packer);
        return target;
    }

    private void process(int width, int height, int depth, Map<SourceNode, TileUnpacker> unpackers, TilePacker packer) {
        var tilesX = Math.ceilDiv(width, TILE_SIZE);
        var tilesY = Math.ceilDiv(height, TILE_SIZE);

        IntStream.range(0, tilesX).boxed()
            .flatMap(x -> IntStream.range(0, tilesY).boxed()
                .flatMap(y -> IntStream.range(0, depth)
                    .mapToObj(z -> new int[]{x, y, z})))
            .parallel()
            .forEach(tile -> processTile(width, height, tile[0], tile[1], tile[2], unpackers, packer));
    }

    private void processTile(
        int width, int height, int tileX, int tileY, int tileZ,
        Map<SourceNode, TileUnpacker> unpackers, TilePacker packer
    ) {
        int tw = Math.min(TILE_SIZE, width - tileX * TILE_SIZE);
        int th = Math.min(TILE_SIZE, height - tileY * TILE_SIZE);
        int sx = tileX * TILE_SIZE;
        int sy = tileY * TILE_SIZE;

        var ctx = new Context(POOL, consumerCounts, unpackers, sx, sy, tileZ, tw, th);
        for (var node : executionOrder) {
            node.process(ctx);
        }
        var out = ctx.get(root);
        packer.pack(out.data(), tw, th, sx, sy, tileZ);
        out.release();
    }
}
