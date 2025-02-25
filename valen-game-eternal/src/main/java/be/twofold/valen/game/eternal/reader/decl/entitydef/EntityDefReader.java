package be.twofold.valen.game.eternal.reader.decl.entitydef;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.game.eternal.*;
import be.twofold.valen.game.eternal.reader.decl.parser.*;
import be.twofold.valen.game.eternal.resource.*;
import com.google.gson.*;

import java.io.*;

public final class EntityDefReader implements AssetReader<JsonObject, EternalAsset> {
    @Override
    public boolean canRead(EternalAsset resource) {
        return resource.id().type() == ResourceType.RsStreamFile
            && resource.id().name().name().startsWith("generated/decls/entitydef/");
    }

    @Override
    public JsonObject read(DataSource source, EternalAsset resource) throws IOException {
        var bytes = source.readBytes(Math.toIntExact(source.size()));
        var parser = new DeclParser(new String(bytes), true);
        return parser.parse();
    }
}
