package be.twofold.valen.game.darkages.reader.decl.renderparm;

import be.twofold.valen.core.game.*;
import be.twofold.valen.game.darkages.*;
import be.twofold.valen.game.darkages.reader.image.*;
import be.twofold.valen.game.idtech.game.*;
import be.twofold.valen.game.idtech.reader.decl.renderparm.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;

public final class RenderParmReader extends AbstractRenderParmReader<DarkAgesAsset> {
    public RenderParmReader() {
        super(TextureMaterialKind::parse);
    }

    @Override
    public boolean canRead(DarkAgesAsset asset) {
        return asset.id().type() == ResourceType.of("rs_streamfile")
            && asset.id().path().startsWith(AssetPaths.RENDER_PARM);
    }

    @Override
    public RenderParm read(BinarySource source, DarkAgesAsset asset, LoadingContext context) throws IOException {
        return read(source);
    }
}
