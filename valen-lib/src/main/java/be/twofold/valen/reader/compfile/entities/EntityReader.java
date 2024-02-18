package be.twofold.valen.reader.compfile.entities;

import be.twofold.valen.core.util.*;
import be.twofold.valen.manager.*;
import be.twofold.valen.reader.*;
import be.twofold.valen.reader.compfile.*;
import be.twofold.valen.reader.decl.parser.*;
import be.twofold.valen.resource.*;

import java.nio.charset.*;
import java.util.*;

public final class EntityReader implements ResourceReader<EntityFile> {
    private final CompFileReader compFileReader;

    public EntityReader(CompFileReader compFileReader) {
        this.compFileReader = compFileReader;
    }

    @Override
    public EntityFile read(BetterBuffer buffer, Resource resource, FileManager manager) {
        byte[] bytes = compFileReader.read(buffer, resource, manager);
        String input = new String(bytes, StandardCharsets.UTF_8);

        var parser = new DeclParser(input);
        parser.expect(DeclTokenType.Name, "Version");
        var version = parser.expectNumber().intValue();

        parser.expect(DeclTokenType.Name, "HierarchyVersion");
        var hierarchyVersion = parser.expectNumber().intValue();

        var entities = new LinkedHashMap<String, Entity>();
        while (!parser.isEof()) {
            parser.expect(DeclTokenType.Name, "entity");
            parser.expect(DeclTokenType.OpenBrace);

            var layers = new ArrayList<String>();
            if (parser.peekToken().value().equals("layers")) {
                parser.expect(DeclTokenType.Name, "layers");
                parser.expect(DeclTokenType.OpenBrace);
                while (parser.peekToken().type() != DeclTokenType.CloseBrace) {
                    layers.add(parser.expectString());
                }
                parser.expect(DeclTokenType.CloseBrace);
            }

            Integer instanceId = null;
            if (parser.peekToken().value().equals("instanceId")) {
                parser.expect(DeclTokenType.Name, "instanceId");
                parser.expect(DeclTokenType.Assign);
                instanceId = parser.expectNumber().intValue();
                parser.expect(DeclTokenType.Semicolon);
            }

            String originalName = null;
            if (parser.peekToken().value().equals("originalName")) {
                parser.expect(DeclTokenType.Name, "originalName");
                parser.expect(DeclTokenType.Assign);
                originalName = parser.expectString();
                parser.expect(DeclTokenType.Semicolon);
            }

            parser.expect(DeclTokenType.Name, "entityDef");
            var name = parser.expectName();
            var entityDef = parser.parseValue().getAsJsonObject();

            var entity = new Entity(layers, instanceId, originalName, entityDef);
            entities.put(name, entity);

            parser.expect(DeclTokenType.CloseBrace);
        }

        return new EntityFile(version, hierarchyVersion, entities);
    }
}
