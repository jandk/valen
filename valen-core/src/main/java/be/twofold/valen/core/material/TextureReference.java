package be.twofold.valen.core.material;

import be.twofold.valen.core.texture.*;
import be.twofold.valen.core.util.*;

import java.io.*;

public record TextureReference(
    String name,
    ThrowingSupplier<Texture, IOException> supplier
) {
    public TextureReference {
        Check.notNull(name, "name");
        Check.notNull(supplier, "supplier");
    }

    @Override
    public String toString() {
        return "TextureReference(" +
            "name=" + name +
            ")";
    }
}
