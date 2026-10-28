package be.twofold.valen.game.greatcircle.reader.decl.renderparm;

import be.twofold.valen.core.game.*;
import be.twofold.valen.game.greatcircle.*;
import be.twofold.valen.game.greatcircle.reader.image.*;
import be.twofold.valen.game.idtech.game.*;
import be.twofold.valen.game.idtech.reader.decl.renderparm.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;

public final class RenderParmReader extends AbstractRenderParmReader<GreatCircleAsset> {
    public RenderParmReader() {
        super(TextureMaterialKind::parse);
    }

    @Override
    public boolean canRead(GreatCircleAsset asset) {
        return asset.id().type() == ResourceType.of("renderparm");
    }

    @Override
    public RenderParm read(BinarySource source, GreatCircleAsset asset, LoadingContext context) throws IOException {
        return read(source);
    }
}
