package be.twofold.valen.export.exr.model;

import java.util.*;

public record ChannelList(
    List<Channel> channels
) {
    public ChannelList {
        channels = List.copyOf(channels);
    }

    public int bytesPerPixel() {
        return channels.stream()
            .mapToInt(channel -> channel.pixelType().size())
            .sum();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ChannelList(\n");
        for (Channel channel : channels) {
            builder.append("    ").append(channel).append("\n");
        }
        return builder.append("  )").toString();
    }
}
