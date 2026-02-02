package be.twofold.valen.game.eternal.reader.binaryfile.blang;

import be.twofold.valen.core.game.*;
import be.twofold.valen.game.eternal.*;
import be.twofold.valen.game.eternal.reader.binaryfile.*;
import be.twofold.valen.game.eternal.resource.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;

public final class BlangReader implements AssetReader<Blang, EternalAsset> {
    private final BinaryFileReader binaryFileReader;

    public BlangReader(BinaryFileReader binaryFileReader) {
        this.binaryFileReader = binaryFileReader;
    }

    @Override
    public boolean canRead(EternalAsset resource) {
        return resource.id().type() == ResourceType.BinaryFile
            && resource.id().extension().equals("blang");
    }

    @Override
    public Blang read(BinarySource source, EternalAsset resource, LoadingContext context) throws IOException {
        var bytes = binaryFileReader.read(source, resource, context);
        return Blang.read(BinarySource.wrap(bytes));
    }
}
