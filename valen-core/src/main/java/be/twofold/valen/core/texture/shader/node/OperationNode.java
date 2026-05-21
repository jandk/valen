package be.twofold.valen.core.texture.shader.node;

import be.twofold.valen.core.texture.shader.*;
import be.twofold.valen.core.texture.shader.operation.*;

import java.util.*;

record OperationNode(
    ShaderNode input,
    Operation operation
) implements ShaderNode {
    @Override
    public void process(Context ctx) {
        var src = ctx.get(input);
        if (src.isLast() && operation instanceof OperationInPlace inPlace) {
            var buf = ctx.steal(input, this);
            inPlace.process(buf.data(), ctx.pixelCount());
        } else {
            var dst = ctx.allocate(this);
            operation.process(src.data(), dst.data(), ctx.pixelCount());
            src.release();
        }
    }

    @Override
    public List<ShaderNode> inputs() {
        return List.of(input);
    }
}
