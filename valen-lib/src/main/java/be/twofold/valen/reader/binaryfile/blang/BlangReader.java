package be.twofold.valen.reader.binaryfile.blang;

import be.twofold.valen.core.util.*;
import be.twofold.valen.reader.*;
import be.twofold.valen.reader.binaryfile.*;
import be.twofold.valen.resource.*;

public final class BlangReader implements ResourceReader<Blang> {
    private final BinaryFileReader binaryFileReader;

    public BlangReader(BinaryFileReader binaryFileReader) {
        this.binaryFileReader = binaryFileReader;
    }

    @Override
    public Blang read(BetterBuffer buffer, Resource resource) {
        byte[] bytes = binaryFileReader.read(buffer, resource);
        return Blang.read(BetterBuffer.wrap(bytes));
    }
}
