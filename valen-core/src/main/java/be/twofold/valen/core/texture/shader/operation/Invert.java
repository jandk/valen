package be.twofold.valen.core.texture.shader.operation;

import be.twofold.valen.core.texture.*;
import wtf.reversed.toolbox.util.*;

import java.util.*;

record Invert(
    Set<Channel> channels
) implements OperationInPlace {
    Invert {
        Check.nonNull(channels, "channels");
        var channelsCopy = EnumSet.noneOf(Channel.class);
        channelsCopy.addAll(channels);
        channels = Collections.unmodifiableSet(channelsCopy);
    }

    @Override
    public void process(float[] buf, int pixelCount) {
        int length = pixelCount * 4;
        for (var channel : channels) {
            for (int i = channel.index(); i < length; i += 4) {
                buf[i] = 1.0f - buf[i];
            }
        }
    }
}
