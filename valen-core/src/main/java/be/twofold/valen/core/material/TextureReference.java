package be.twofold.valen.core.material;

import be.twofold.valen.core.texture.*;
import be.twofold.valen.core.util.*;
import wtf.reversed.toolbox.util.*;

import java.io.*;

public record TextureReference(
    String name,
    String filename,
    ThrowingSupplier<Texture, IOException> supplier
) {
    public TextureReference {
        Check.nonNull(name, "name");
        Check.nonNull(filename, "filename");
        Check.nonNull(supplier, "supplier");
    }

    @Override
    public String toString() {
        return "TextureReference(" +
            "name=" + name + ", " +
            "filename=" + filename + ")";
    }
}
