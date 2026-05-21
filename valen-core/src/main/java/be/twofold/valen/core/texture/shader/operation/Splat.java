package be.twofold.valen.core.texture.shader.operation;

import be.twofold.valen.core.texture.*;
import wtf.reversed.toolbox.util.*;

import java.util.*;

record Splat(
    Channel source,
    List<Channel> targets
) implements OperationInPlace {
    Splat {
        Check.nonNull(source, "source");
        targets = List.copyOf(targets);
    }

    @Override
    public void process(float[] buf, int pixelCount) {
        int srcIndex = source.index();
        for (var target : EnumSet.copyOf(targets)) {
            if (source == target) {
                continue;
            }
            int tgtIndex = target.index();
            for (int i = 0; i < pixelCount * 4; i += 4) {
                buf[i + tgtIndex] = buf[i + srcIndex];
            }
        }
    }
}
