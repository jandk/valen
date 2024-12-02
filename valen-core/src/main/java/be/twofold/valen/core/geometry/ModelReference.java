package be.twofold.valen.core.geometry;

import be.twofold.valen.core.util.*;
import be.twofold.valen.core.util.fi.*;

import java.io.*;

public record ModelReference(
    String name,
    ThrowingSupplier<Model, IOException> supplier
) {
    public ModelReference {
        Check.notNull(name, "name");
        Check.notNull(supplier, "supplier");
    }
}
