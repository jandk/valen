package be.twofold.valen.game.source.readers.vmt;

import be.twofold.valen.core.game.*;
import be.twofold.valen.game.source.*;
import be.twofold.valen.game.source.readers.keyvalue.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;

public final class VmtReader implements AssetReader<ValveMaterial, SourceAsset> {
    @Override
    public boolean canRead(SourceAsset asset) {
        return asset.id().extension().equals("vmt");
    }

    @Override
    public ValveMaterial read(BinarySource source, SourceAsset asset, LoadingContext context) throws IOException {
        var string = source.readString(Math.toIntExact(source.size()));
        var keyValue = KeyValue.parse(string);
        return ValveMaterial.read(keyValue);
    }
}
