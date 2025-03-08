package org.redeye.valen.game.source1.readers;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.io.*;
import org.redeye.valen.game.source1.*;
import org.redeye.valen.game.source1.utils.keyvalues.*;
import org.redeye.valen.game.source1.vmt.*;

import java.io.*;

public final class VmtReader implements AssetReader<ValveMaterial, SourceAsset> {
    @Override
    public boolean canRead(SourceAsset asset) {
        return asset.id().extension().equals("vmt");
    }

    @Override
    public ValveMaterial read(DataSource source, SourceAsset asset) throws IOException {
        var string = source.readString(Math.toIntExact(source.size()));
        var vdfValue = new VdfReader(new StringReader(string)).parse();
        return ValveMaterial.fromVdf(vdfValue.asObject());
    }
}
