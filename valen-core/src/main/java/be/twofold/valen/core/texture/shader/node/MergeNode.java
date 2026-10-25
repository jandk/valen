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
        writeChannel(ctx, r, dst, 0);
        writeChannel(ctx, g, dst, 1);
        writeChannel(ctx, b, dst, 2);
        writeChannel(ctx, a, dst, 3);
    }

    @Override
    public List<ShaderNode> inputs() {
        return Stream.of(r, g, b, a)
            .filter(cs -> cs instanceof Source.From)
            .map(cs -> ((Source.From) cs).node())
            .toList();
    }

    private void writeChannel(Context ctx, Source src, float[] dst, int dstIndex) {
        switch (src) {
            case Source.Const source -> writeConstant(ctx, source, dst, dstIndex);
            case Source.From source -> writeFrom(ctx, source, dst, dstIndex);
        }
    }

    private void writeConstant(Context ctx, Source.Const source, float[] dst, int dstIndex) {
        var value = source.value();
        for (int i = dstIndex, len = ctx.pixelCount() * 4; i < len; i += 4) {
            dst[i] = value;
        }
    }

    private void writeFrom(Context ctx, Source.From source, float[] dst, int dstIndex) {
        var buf = ctx.get(source.node());
        var src = buf.data();

        var srcIndex = source.channel().index();
        for (int i = 0, len = ctx.pixelCount() * 4; i < len; i += 4) {
            dst[i + dstIndex] = src[i + srcIndex];
        }

        buf.release();
    }

    private sealed interface Source {
        record From(ShaderNode node, Channel channel) implements Source {
            public From {
                Check.nonNull(node, "node");
                Check.nonNull(channel, "channel");
            }
        }

        record Const(float value) implements Source {
        }
    }

    public static final class Builder {
        private Source r = new Source.Const(0.0f);
        private Source g = new Source.Const(0.0f);
        private Source b = new Source.Const(0.0f);
        private Source a = new Source.Const(1.0f);

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
