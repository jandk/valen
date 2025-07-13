package be.twofold.valen.game.darkages.reader.decl.renderparm;

import be.twofold.valen.core.io.BinaryReader;
import be.twofold.valen.game.darkages.*;
import be.twofold.valen.game.darkages.reader.resources.*;
import be.twofold.valen.game.idtech.renderparm.*;

import java.io.*;

public final class RenderParmReader extends AbstractRenderParmReader<DarkAgesAsset> {
    @Override
    public boolean canRead(DarkAgesAsset asset) {
        return asset.id().type() == ResourcesType.RsStreamFile
            && asset.id().name().name().startsWith("generated/decls/renderparm/");
    }

    @Override
    public RenderParm read(BinaryReader reader, DarkAgesAsset asset) throws IOException {
        return read(reader);
    }
}
