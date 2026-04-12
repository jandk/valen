package be.twofold.valen.core.texture.shader.node;

import be.twofold.valen.core.texture.*;
import be.twofold.valen.core.texture.shader.*;
import be.twofold.valen.core.texture.shader.operation.*;

import java.util.*;
import java.util.function.*;

public sealed interface ShaderNode permits MergeNode, OperationNode, SourceNode {

    void process(Context ctx);

    List<ShaderNode> inputs();

    static SourceNode source() {
        return new SourceNode();
    }

    static ShaderNode merge(Consumer<MergeNode.Builder> builderConsumer) {
        var builder = new MergeNode.Builder();
        builderConsumer.accept(builder);
        return builder.build();
    }

    static ShaderNode scaleAndBias(ShaderNode input, float scale, float bias) {
        if (scale == 1.0f && bias == 0.0f) {
            return input;
        }
        return new OperationNode(input, Operation.scaleAndBias(scale, bias));
    }

    static ShaderNode reconstructZ(ShaderNode input) {
        return new OperationNode(input, Operation.reconstructZ());
    }

    static ShaderNode splat(ShaderNode input, Channel source, Channel... targets) {
        return new OperationNode(input, Operation.splat(source, targets));
    }

}
