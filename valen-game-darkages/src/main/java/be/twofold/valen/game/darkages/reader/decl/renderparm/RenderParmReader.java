package be.twofold.valen.game.darkages.reader.decl.renderparm;

import be.twofold.valen.core.game.*;
import be.twofold.valen.game.darkages.*;
import be.twofold.valen.game.darkages.reader.resources.*;
import be.twofold.valen.game.idtech.renderparm.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;

public final class RenderParmReader extends AbstractRenderParmReader<DarkAgesAsset> {
    @Override
    public boolean canRead(DarkAgesAsset asset) {
        return asset.id().type() == ResourcesType.RsStreamFile
            && asset.id().name().name().startsWith("generated/decls/renderparm/");
    }

    @Override
    public RenderParm read(BinarySource source, DarkAgesAsset asset, LoadingContext context) throws IOException {
        return read(source);
    }
}
