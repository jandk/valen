package org.redeye.valen.game.spacemarines2.readers;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.io.*;
import org.redeye.valen.game.spacemarines2.*;
import org.redeye.valen.game.spacemarines2.archives.*;
import org.redeye.valen.game.spacemarines2.types.*;
import org.redeye.valen.game.spacemarines2.types.lwi.*;

import java.io.*;

public class LwiContainerReader implements Reader<LwiContainer> {
    @Override
    public LwiContainer read(EmperorArchive archive, Asset asset, DataSource source) throws IOException {
        ResourceHeader ignored = ResourceHeader.read(source);
        LwiHeader header = LwiHeader.read(source);
        if (header.containerType().equals("lwi_container_static")) {
            return LwiContainerStatic.read(source, header.version());
        } else {
            throw new IllegalStateException("LwiContainerReader only supports lwi_container_static, got " + header.containerType());
        }
    }

    @Override
    public boolean canRead(AssetID asset) {
        return (asset instanceof EmperorAssetId) && asset.fileName().endsWith(".lwi_container");
    }
}