package be.twofold.valen.export.exr;

import be.twofold.valen.core.math.*;
import be.twofold.valen.export.exr.model.*;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

final class ExrHeader {
    private final Map<String, ExrAttribute> attributes;

    public ExrHeader(List<ExrAttribute> attributes) {
        this.attributes = attributes.stream()
            .collect(Collectors.toMap(ExrAttribute::name, Function.identity(), (first, second) -> {
                throw new IllegalStateException(String.format("Duplicate key %s", first));
            }, TreeMap::new));
    }

    public <T> T get(ExrAttributeType<T> type) {
        ExrAttribute attribute = attributes.get(type.name());
        if (attribute == null) {
            throw new IllegalArgumentException("Attribute " + type.name() + " not found");
        }

        return type.clazz().cast(attribute.value());
    }

    public ChannelList channels() {
        return get(ExrAttributeType.CHANNELS);
    }

    public Compression compression() {
        return get(ExrAttributeType.COMPRESSION);
    }

    public Box2i dataWindow() {
        return get(ExrAttributeType.DATA_WINDOW);
    }

    public Box2i displayWindow() {
        return get(ExrAttributeType.DISPLAY_WINDOW);
    }

    public LineOrder lineOrder() {
        return get(ExrAttributeType.LINE_ORDER);
    }

    public float pixelAspectRatio() {
        return get(ExrAttributeType.PIXEL_ASPECT_RATIO);
    }

    public Vector2 screenWindowCenter() {
        return get(ExrAttributeType.SCREEN_WINDOW_CENTER);
    }

    public float screenWindowWidth() {
        return get(ExrAttributeType.SCREEN_WINDOW_WIDTH);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ExrHeader(\n");
        for (Map.Entry<String, ExrAttribute> entry : attributes.entrySet()) {
            builder.append("  ").append(entry.getKey()).append(": ").append(entry.getValue().value()).append("\n");
        }
        return builder.append(")").toString();
    }
}
