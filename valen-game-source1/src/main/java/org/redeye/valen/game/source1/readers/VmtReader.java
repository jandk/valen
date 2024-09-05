package org.redeye.valen.game.source1.readers;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.io.*;
import org.redeye.valen.game.source1.*;
import org.redeye.valen.game.source1.utils.keyvalues.*;
import org.redeye.valen.game.source1.vmt.*;

import java.io.*;

public class VmtReader implements Reader<ValveMaterial> {

    @Override
    public ValveMaterial read(Archive archive, Asset asset, DataSource source) throws IOException {
        byte[] bytes = source.readBytes(Math.toIntExact(source.size()));
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        InputStreamReader streamReader = new InputStreamReader(in);
        VdfReader reader = new VdfReader(streamReader);
        return ValveMaterial.fromVdf(reader.parse().asObject());
    }

    @Override
    public boolean canRead(Asset asset) {
        if (asset.id() instanceof SourceAssetID sid) {
            return sid.extension().equals("vmt");
        }
        return false;
    }
}
