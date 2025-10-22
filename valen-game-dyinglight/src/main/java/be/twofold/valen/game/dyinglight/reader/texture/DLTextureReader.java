package be.twofold.valen.game.dyinglight.reader.texture;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.core.texture.*;
import be.twofold.valen.game.dyinglight.*;
import be.twofold.valen.game.dyinglight.reader.rpack.*;

import java.io.*;

public class DLTextureReader implements AssetReader<Texture, DyingLightAsset> {
    @Override
    public boolean canRead(DyingLightAsset asset) {
        return asset.id().type() == ResourceType.Texture;
    }


    @Override
    public Texture read(BinaryReader reader, DyingLightAsset asset) throws IOException {
        var header = DLTextureHeader.read(reader);
        if (header.width() == 0 || header.height() == 0) {
            return null;
        }
        long dataOffset = 0;
        if (asset.hasSection(ResourceType.TextureBitmapData)) {
            dataOffset = asset.sectionOffset(ResourceType.TextureBitmapData);
        } else {
            dataOffset = asset.sectionOffset(ResourceType.Texture) + 80;
        }
        TextureFormat format = header.format().textureFormat();
        var size = format.surfaceSize(header.width(), header.height());
        reader.position(dataOffset);
        var rawData = reader.readBytes(size);
        var surface = new Surface(header.width(), header.height(), format, rawData);
        return Texture.fromSurface(surface, format);
    }
}
