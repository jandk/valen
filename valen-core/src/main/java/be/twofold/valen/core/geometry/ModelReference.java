package be.twofold.valen.core.geometry;

import be.twofold.valen.core.util.*;

import java.io.*;

public record ModelReference(
    String name,
    String filename,
    ThrowingSupplier<Model, IOException> supplier
) {
    public ModelReference {
        Check.nonNull(name, "name");
        Check.nonNull(filename, "filename");
        Check.nonNull(supplier, "supplier");
    }

    @Override
    public String toString() {
        return "ModelReference(" + name + ")";
    }
}
