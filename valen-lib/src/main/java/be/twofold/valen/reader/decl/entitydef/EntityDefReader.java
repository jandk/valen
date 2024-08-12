package be.twofold.valen.reader.decl.entitydef;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.reader.*;
import be.twofold.valen.reader.decl.parser.*;
import be.twofold.valen.resource.*;
import com.google.gson.*;

import java.io.*;

public final class EntityDefReader implements ResourceReader<JsonObject> {
    @Override
    public boolean canRead(ResourceKey key) {
        return key.type() == ResourceType.RsStreamFile
            && key.name().name().startsWith("generated/decls/entitydef/");
    }

    @Override
    public JsonObject read(DataSource source, Asset<ResourceKey> asset) throws IOException {
        var bytes = source.readBytes(Math.toIntExact(source.size()));
        var parser = new DeclParser(new String(bytes), true);
        return parser.parse();
    }
}
