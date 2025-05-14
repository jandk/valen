package be.twofold.valen.game.darkages.reader.decl.renderparm;

import be.twofold.valen.core.io.*;
import be.twofold.valen.game.darkages.*;
import be.twofold.valen.game.darkages.reader.resources.*;
import be.twofold.valen.game.idtech.renderparm.*;

import java.io.*;

public final class RenderParmReader extends AbstractRenderParmReader<DarkAgesAsset> {
    @Override
    public boolean canRead(DarkAgesAsset resource) {
        return resource.id().type() == ResourcesType.RsStreamFile
            && resource.id().name().name().startsWith("generated/decls/renderparm/");
    }

    @Override
    public RenderParm read(DataSource source, DarkAgesAsset resource) throws IOException {
        return read(source);
    }
}
