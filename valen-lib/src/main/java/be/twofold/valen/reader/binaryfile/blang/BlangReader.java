package be.twofold.valen.reader.binaryfile.blang;

import be.twofold.valen.core.io.*;
import be.twofold.valen.reader.*;
import be.twofold.valen.reader.binaryfile.*;
import be.twofold.valen.resource.*;

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
    public Blang read(DataSource source, Resource resource) throws IOException {
        byte[] bytes = binaryFileReader.read(source, resource);
        return Blang.read(new ByteArrayDataSource(bytes));
    }
}
