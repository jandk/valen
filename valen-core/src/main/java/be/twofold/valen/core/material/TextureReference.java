package be.twofold.valen.core.material;

import be.twofold.valen.core.texture.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.core.util.fi.*;

import java.io.*;

public record TextureReference(
    String name,
    TextureType type,
    ThrowingSupplier<Texture, IOException> supplier
) {
    public TextureReference {
        Check.notNull(name, "name");
        Check.notNull(type, "type");
        Check.notNull(supplier, "supplier");
    }
}
