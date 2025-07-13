package be.twofold.valen.game.eternal.reader.decl.renderparm;

import be.twofold.valen.core.io.BinaryReader;
import be.twofold.valen.game.eternal.*;
import be.twofold.valen.game.eternal.resource.*;
import be.twofold.valen.game.idtech.renderparm.*;

import java.io.*;

public final class RenderParmReader extends AbstractRenderParmReader<EternalAsset> {
    @Override
    public boolean canRead(EternalAsset resource) {
        return resource.id().type() == ResourceType.RsStreamFile
            && resource.id().name().name().startsWith("generated/decls/renderparm/");
    }

    @Override
    public RenderParm read(BinaryReader reader, EternalAsset resource) throws IOException {
        return read(reader);
    }
}
