package be.twofold.valen.core.geometry;

import be.twofold.valen.core.util.*;

import java.io.*;

public record ModelReference(
    String name,
    ThrowingSupplier<Model, IOException> supplier
) {
    public ModelReference {
        Check.notNull(name, "name");
        Check.notNull(supplier, "supplier");
    }

    @Override
    public String toString() {
        return "ModelReference(" + name + ")";
    }
}
