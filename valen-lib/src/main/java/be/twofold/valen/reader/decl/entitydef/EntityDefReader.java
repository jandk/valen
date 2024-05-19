package be.twofold.valen.reader.decl.entitydef;

import be.twofold.valen.core.io.*;
import be.twofold.valen.reader.*;
import be.twofold.valen.reader.decl.parser.*;
import be.twofold.valen.resource.*;
import com.google.gson.*;
import jakarta.inject.*;

import java.io.*;

public final class EntityDefReader implements ResourceReader<JsonObject> {

    @Inject
    public EntityDefReader() {
    }

    @Override
    public boolean canRead(Resource entry) {
        return entry.type() == ResourceType.RsStreamFile
            && entry.nameString().startsWith("generated/decls/entitydef/");
    }

    @Override
    public JsonObject read(DataSource source, Resource resource) throws IOException {
        var bytes = source.readBytes(Math.toIntExact(source.size()));
        var parser = new DeclParser(new String(bytes), true);
        return parser.parse();
    }
}
