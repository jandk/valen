package be.twofold.valen.export.exr;

import be.twofold.valen.core.math.*;
import be.twofold.valen.export.exr.model.*;

import java.util.*;

final class ExrAttributeType<T> {
    public static final ExrAttributeType<ChannelList> CHANNELS = new ExrAttributeType<>("channels", "chlist", ChannelList.class);
    public static final ExrAttributeType<Compression> COMPRESSION = new ExrAttributeType<>("compression", "compression", Compression.class);
    public static final ExrAttributeType<Box2i> DATA_WINDOW = new ExrAttributeType<>("dataWindow", "box2i", Box2i.class);
    public static final ExrAttributeType<Box2i> DISPLAY_WINDOW = new ExrAttributeType<>("displayWindow", "box2i", Box2i.class);
    public static final ExrAttributeType<LineOrder> LINE_ORDER = new ExrAttributeType<>("lineOrder", "lineOrder", LineOrder.class);
    public static final ExrAttributeType<Float> PIXEL_ASPECT_RATIO = new ExrAttributeType<>("pixelAspectRatio", "float", Float.class);
    public static final ExrAttributeType<Vector2> SCREEN_WINDOW_CENTER = new ExrAttributeType<>("screenWindowCenter", "v2f", Vector2.class);
    public static final ExrAttributeType<Float> SCREEN_WINDOW_WIDTH = new ExrAttributeType<>("screenWindowWidth", "float", Float.class);

    private final String name;
    private final String type;
    private final Class<T> clazz;

    private ExrAttributeType(String name, String type, Class<T> clazz) {
        this.name = Objects.requireNonNull(name);
        this.type = Objects.requireNonNull(type);
        this.clazz = Objects.requireNonNull(clazz);
    }

    public String name() {
        return name;
    }

    public String type() {
        return type;
    }

    public Class<T> clazz() {
        return clazz;
    }

    @Override
    public String toString() {
        return "ExrAttributeType(" +
            "name='" + name + "', " +
            "type='" + type + "'" +
            "clazz=" + clazz +
            ")";
    }
}
