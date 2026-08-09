package be.twofold.valen.game.eternal.reader.binaryfile.blang;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.game.eternal.*;
import be.twofold.valen.game.eternal.reader.binaryfile.*;
import be.twofold.valen.game.eternal.resource.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.lang.invoke.*;
import java.util.*;

public final class BlangReader implements AssetReader.Binary<Blang, EternalAsset> {
    private final BinaryFileReader binaryFileReader;

    public BlangReader(BinaryFileReader binaryFileReader) {
        this.binaryFileReader = binaryFileReader;
    }

    @Override
    public boolean canRead(EternalAsset asset) {
        return asset.id().type() == ResourceType.BinaryFile
            && asset.id().extension().equals("blang");
    }

    @Override
    public Blang read(BinarySource source, EternalAsset asset, LoadingContext context) throws IOException {
        var bytes = binaryFileReader.read(source, asset, context);
        return Blang.read(BinarySource.wrap(bytes));
    }

    @Override
    public Optional<Meta.Node> readMetadata(EternalAsset asset, LoadingContext context) throws IOException {
        try (var source = BinarySource.wrap(context.open(asset.location()))) {
            var bytes = binaryFileReader.read(source, asset, context);
            var blang = Blang.read(BinarySource.wrap(bytes));
            return Optional.of(Meta.build(MethodHandles.lookup(), blang));
        }
    }
}
