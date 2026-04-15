package be.twofold.valen.core.texture.shader.node;

import be.twofold.valen.core.texture.*;
import be.twofold.valen.core.texture.shader.*;
import wtf.reversed.toolbox.util.*;

import java.util.*;
import java.util.stream.*;

public record MergeNode(
    Source r,
    Source g,
    Source b,
    Source a
) implements ShaderNode {
    @Override
    public void process(Context ctx) {
        var out = ctx.allocate(this);
        var dst = out.data();
        writeChannel(r, 0, dst, ctx);
        writeChannel(g, 1, dst, ctx);
        writeChannel(b, 2, dst, ctx);
        writeChannel(a, 3, dst, ctx);
    }

    @Override
    public List<ShaderNode> inputs() {
        return Stream.of(r, g, b, a)
            .filter(cs -> cs instanceof Source.From)
            .map(cs -> ((Source.From) cs).node())
            .toList();
    }

    private void writeChannel(Source src, int dstIndex, float[] dst, Context ctx) {
        switch (src) {
            case Source.Const(var value) -> writeConstant(ctx, dst, dstIndex, value);
            case Source.From(var node, var channel) -> writeFrom(ctx, dst, dstIndex, node, channel);
        }
    }

    private void writeConstant(Context ctx, float[] dst, int dstIndex, float value) {
        for (int i = dstIndex, len = ctx.pixelCount() * 4; i < len; i += 4) {
            dst[i] = value;
        }
    }

    private void writeFrom(Context ctx, float[] dst, int dstIndex, ShaderNode node, Channel channel) {
        var buf = ctx.get(node);
        var src = buf.data();

        var srcIndex = channel.index();
        for (int i = 0, len = ctx.pixelCount() * 4; i < len; i += 4) {
            dst[i + dstIndex] = src[i + srcIndex];
        }

        buf.release();
    }

    private sealed interface Source {
        record From(
            ShaderNode node,
            Channel channel
        ) implements Source {
            public From {
                Check.nonNull(node, "node");
                Check.nonNull(channel, "channel");
            }
        }

        record Const(float value) implements Source {
            static final Const ZERO = new Const(0.0f);
            static final Const UNIT = new Const(1.0f);
        }
    }

    public static final class Builder {
        private Source r = Source.Const.ZERO;
        private Source g = Source.Const.ZERO;
        private Source b = Source.Const.ZERO;
        private Source a = Source.Const.UNIT;

        public Builder channel(Channel src, ShaderNode node, Channel dst) {
            set(dst, new Source.From(node, src));
            return this;
        }

        public Builder channel(Channel dst, float value) {
            set(dst, new Source.Const(value));
            return this;
        }

        private void set(Channel dst, Source source) {
            switch (dst) {
                case RED -> r = source;
                case GREEN -> g = source;
                case BLUE -> b = source;
                case ALPHA -> a = source;
            }
        }

        MergeNode build() {
            return new MergeNode(r, g, b, a);
        }
    }
}
