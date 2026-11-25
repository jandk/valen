package be.twofold.valen.core.export;

import be.twofold.valen.core.texture.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;

public abstract class TextureExporter implements Exporter<Texture> {
    private final boolean useMips;
    private boolean reconstructZ = true;

    protected TextureExporter(boolean useMips) {
        this.useMips = useMips;
    }

    @Override
    public void setProperty(String key, Object value) {
        if (key.equals("reconstructZ")) {
            reconstructZ = (Boolean) value;
        }
    }

    @Override
    public void export(Texture texture, BinaryWriter writer) throws IOException {
        if (!useMips) {
            texture = texture.firstOnly();
        }

        var chosenFormat = chooseFormat(texture.format());
        var converted = texture.convert(chosenFormat, reconstructZ);
        doExport(converted, writer);
    }

    protected abstract TextureFormat chooseFormat(TextureFormat format);

    protected abstract void doExport(Texture texture, BinaryWriter writer) throws IOException;
}
