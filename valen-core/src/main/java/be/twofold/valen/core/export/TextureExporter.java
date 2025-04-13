package be.twofold.valen.core.export;

import be.twofold.valen.core.texture.*;

import java.io.*;

public abstract class TextureExporter implements Exporter<Texture> {
    public abstract TextureFormat chooseFormat(TextureFormat format);

    public abstract void doExport(Texture texture, OutputStream out) throws IOException;

    @Override
    public void export(Texture texture, OutputStream out) throws IOException {
        var chosenFormat = chooseFormat(texture.format());
        var converted = texture.firstOnly().convert(chosenFormat);
        doExport(converted, out);
    }
}
