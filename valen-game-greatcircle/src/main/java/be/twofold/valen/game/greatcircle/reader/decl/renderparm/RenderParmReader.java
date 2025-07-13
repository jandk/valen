package be.twofold.valen.game.greatcircle.reader.decl.renderparm;

import be.twofold.valen.core.io.BinaryReader;
import be.twofold.valen.game.greatcircle.*;
import be.twofold.valen.game.greatcircle.resource.*;
import be.twofold.valen.game.idtech.renderparm.*;

import java.io.*;

public final class RenderParmReader extends AbstractRenderParmReader<GreatCircleAsset> {
    @Override
    public boolean canRead(GreatCircleAsset asset) {
        return asset.id().type() == ResourceType.renderparm;
    }

    @Override
    public RenderParm read(BinaryReader reader, GreatCircleAsset asset) throws IOException {
        return read(reader);
    }
}
