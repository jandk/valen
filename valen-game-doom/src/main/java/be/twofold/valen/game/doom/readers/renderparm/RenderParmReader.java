package be.twofold.valen.game.doom.readers.renderparm;

import be.twofold.valen.core.game.*;
import be.twofold.valen.game.doom.*;
import be.twofold.valen.game.idtech.game.*;
import be.twofold.valen.game.idtech.reader.decl.renderparm.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.util.*;

public final class RenderParmReader extends AbstractRenderParmReader<DoomAsset> {
    public RenderParmReader() {
        // No material kinds in DOOM (2016)
        super(_ -> Optional.empty());
    }

    @Override
    public boolean canRead(DoomAsset asset) {
        return asset.id().type() == ResourceType.of("renderParm");
    }

    @Override
    public RenderParm read(BinaryReader reader, DoomAsset asset, LoadingContext context) throws IOException {
        return read(reader);
    }
}
