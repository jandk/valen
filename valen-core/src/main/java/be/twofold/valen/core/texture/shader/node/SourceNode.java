package be.twofold.valen.core.texture.shader.node;

import be.twofold.valen.core.texture.*;
import be.twofold.valen.core.texture.shader.*;
import wtf.reversed.toolbox.util.*;

import java.util.*;

public record SourceNode() implements ShaderNode {
    @Override
    public void process(Context ctx) {
        var buf = ctx.allocate(this);
        ctx.unpack(this, buf.data());
    }

    @Override
    public List<ShaderNode> inputs() {
        return List.of();
    }

    public Bind bind(Surface surface) {
        return new Bind(this, surface);
    }

    public record Bind(
        SourceNode source,
        Surface surface
    ) {
        public Bind {
            Check.nonNull(source, "source");
            Check.nonNull(surface, "surface");
        }
    }
}
