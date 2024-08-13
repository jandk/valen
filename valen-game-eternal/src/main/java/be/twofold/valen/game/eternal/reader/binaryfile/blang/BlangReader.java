package be.twofold.valen.game.eternal.reader.binaryfile.blang;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.game.eternal.reader.*;
import be.twofold.valen.game.eternal.reader.binaryfile.*;
import be.twofold.valen.game.eternal.resource.*;

import java.io.*;

public final class BlangReader implements ResourceReader<Blang> {
    private final BinaryFileReader binaryFileReader;

    public BlangReader(BinaryFileReader binaryFileReader) {
        this.binaryFileReader = binaryFileReader;
    }

    @Override
    public boolean canRead(ResourceKey key) {
        return key.type() == ResourceType.BinaryFile
            && key.name().extension().equals("blang");
    }

    @Override
    public Blang read(DataSource source, Asset asset) throws IOException {
        byte[] bytes = binaryFileReader.read(source, asset);
        return Blang.read(new ByteArrayDataSource(bytes));
    }
}
