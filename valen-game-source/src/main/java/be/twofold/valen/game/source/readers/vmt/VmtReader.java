package be.twofold.valen.game.source.readers.vmt;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.game.source.*;
import be.twofold.valen.game.source.readers.keyvalue.*;

import java.io.*;

public final class VmtReader implements AssetReader<ValveMaterial, SourceAsset> {
    @Override
    public boolean canRead(SourceAsset asset) {
        return asset.id().extension().equals("vmt");
    }

    @Override
    public ValveMaterial read(DataSource source, SourceAsset asset) throws IOException {
        var string = source.readString(Math.toIntExact(source.size()));
        var keyValue = KeyValue.parse(string);
        return ValveMaterial.read(keyValue);
    }
}
