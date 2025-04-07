package be.twofold.valen.export.exr;

import be.twofold.valen.core.export.*;
import be.twofold.valen.core.math.*;
import be.twofold.valen.core.texture.*;
import be.twofold.valen.export.exr.model.*;

import java.io.*;
import java.util.*;

public final class ExrExporter implements Exporter<Texture> {
    @Override
    public String getID() {
        return "texture.exr";
    }

    @Override
    public String getName() {
        return "OpenEXR";
    }

    @Override
    public String getExtension() {
        return "exr";
    }

    @Override
    public Class<Texture> getSupportedType() {
        return Texture.class;
    }

    @Override
    public void export(Texture value, OutputStream out) throws IOException {

    }

    private ExrHeader buildHeader(Texture texture) {
        List<ExrAttribute> attributes = new ArrayList<>();
        attributes.add(ExrAttribute.create(ExrAttributeType.CHANNELS, null));
        attributes.add(ExrAttribute.create(ExrAttributeType.COMPRESSION, Compression.ZIP_COMPRESSION));
        attributes.add(ExrAttribute.create(ExrAttributeType.DATA_WINDOW, new Box2i(0, 0, texture.width(), texture.height())));
        attributes.add(ExrAttribute.create(ExrAttributeType.DISPLAY_WINDOW, new Box2i(0, 0, texture.width(), texture.height())));
        attributes.add(ExrAttribute.create(ExrAttributeType.LINE_ORDER, LineOrder.INCREASING_Y));
        attributes.add(ExrAttribute.create(ExrAttributeType.PIXEL_ASPECT_RATIO, 1.0f));
        attributes.add(ExrAttribute.create(ExrAttributeType.SCREEN_WINDOW_CENTER, Vector2.Zero));
        attributes.add(ExrAttribute.create(ExrAttributeType.SCREEN_WINDOW_WIDTH, 1.0f));
        return new ExrHeader(attributes);
    }
}
