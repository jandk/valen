package be.twofold.valen.core.export;

import be.twofold.valen.core.texture.*;

public interface TextureExporter extends Exporter<Texture> {
    TextureFormat chooseFormat(TextureFormat format);
}
