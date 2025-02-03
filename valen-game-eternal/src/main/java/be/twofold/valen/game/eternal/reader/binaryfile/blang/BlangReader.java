package be.twofold.valen.game.eternal.reader.binaryfile.blang;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.game.eternal.reader.binaryfile.*;
import be.twofold.valen.game.eternal.resource.*;

import java.io.*;

public final class BlangReader implements AssetReader<Blang, Resource> {
    private final BinaryFileReader binaryFileReader;

    public BlangReader(BinaryFileReader binaryFileReader) {
        this.binaryFileReader = binaryFileReader;
    }

    @Override
    public boolean canRead(Resource resource) {
        return resource.key().type() == ResourceType.BinaryFile
            && resource.key().name().extension().equals("blang");
    }

    @Override
    public Blang read(DataSource source, Resource resource) throws IOException {
        byte[] bytes = binaryFileReader.read(source, resource);
        return Blang.read(DataSource.fromArray(bytes));
    }
}
