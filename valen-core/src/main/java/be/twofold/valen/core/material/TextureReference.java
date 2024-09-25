package be.twofold.valen.core.material;

import be.twofold.valen.core.texture.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.core.util.fi.*;

import java.io.*;

public record TextureReference(
    TextureType type,
    String filename,
    ThrowingSupplier<Texture, IOException> supplier
) {
    public TextureReference {
        Check.notNull(type, "type");
        Check.notNull(filename, "filename");
        Check.notNull(supplier, "supplier");
    }
}
