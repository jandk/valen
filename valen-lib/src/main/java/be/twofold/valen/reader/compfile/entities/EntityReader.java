package be.twofold.valen.reader.compfile.entities;

import be.twofold.valen.core.util.*;
import be.twofold.valen.reader.*;
import be.twofold.valen.reader.compfile.*;
import be.twofold.valen.reader.decl.parser.*;
import be.twofold.valen.resource.*;
import jakarta.inject.*;

import java.nio.charset.*;
import java.util.*;

public final class EntityReader implements ResourceReader<EntityFile> {
    private final CompFileReader compFileReader;

    @Inject
    public EntityReader(CompFileReader compFileReader) {
        this.compFileReader = compFileReader;
    }

    @Override
    public boolean canRead(Resource entry) {
        return entry.type() == ResourceType.CompFile
               && entry.name().extension().equals("entities");
    }

    @Override
    public EntityFile read(BetterBuffer buffer, Resource resource) {
        byte[] bytes = compFileReader.read(buffer, resource);
        String input = new String(bytes, StandardCharsets.UTF_8);

        var parser = new DeclParser(input);
        parser.expectName("Version");
        var version = parser.expectNumber().intValue();

        parser.expectName("HierarchyVersion");
        var hierarchyVersion = parser.expectNumber().intValue();

        var entities = new LinkedHashMap<String, Entity>();
        while (!parser.match(DeclTokenType.Eof)) {
            parser.expectName("entity");
            parser.expect(DeclTokenType.OpenBrace);

            var layers = new ArrayList<String>();
            if (parser.matchName("layers")) {
                parser.expect(DeclTokenType.OpenBrace);
                while (parser.match(DeclTokenType.CloseBrace)) {
                    layers.add(parser.expectString());
                }
                parser.expect(DeclTokenType.CloseBrace);
            }

            Integer instanceId = null;
            if (parser.matchName("instanceId")) {
                parser.expect(DeclTokenType.Assign);
                instanceId = parser.expectNumber().intValue();
                parser.expect(DeclTokenType.Semicolon);
            }

            String originalName = null;
            if (parser.matchName("originalName")) {
                parser.expect(DeclTokenType.Assign);
                originalName = parser.expectString();
                parser.expect(DeclTokenType.Semicolon);
            }

            parser.expectName("entityDef");
            var name = parser.expectName();
            var entityDef = parser.parseValue().getAsJsonObject();

            var entity = new Entity(layers, instanceId, originalName, entityDef);
            entities.put(name, entity);

            parser.expect(DeclTokenType.CloseBrace);
        }

        return new EntityFile(version, hierarchyVersion, entities);
    }
}
