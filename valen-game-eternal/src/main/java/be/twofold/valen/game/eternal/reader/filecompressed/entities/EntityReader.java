package be.twofold.valen.game.eternal.reader.filecompressed.entities;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.game.eternal.*;
import be.twofold.valen.game.eternal.reader.filecompressed.*;
import be.twofold.valen.game.eternal.resource.*;
import be.twofold.valen.game.idtech.decl.parser.*;

import java.io.*;
import java.nio.charset.*;
import java.util.*;

public final class EntityReader implements AssetReader<EntityFile, EternalAsset> {
    private final FileCompressedReader fileCompressedReader;

    public EntityReader(FileCompressedReader fileCompressedReader) {
        this.fileCompressedReader = fileCompressedReader;
    }

    @Override
    public boolean canRead(EternalAsset resource) {
        return resource.id().type() == ResourceType.CompFile
            && resource.id().extension().equals("entities");
    }

    @Override
    public EntityFile read(BinarySource source, EternalAsset resource) throws IOException {
        var bytes = fileCompressedReader.read(source, resource);
        var input = StandardCharsets.UTF_8.decode(bytes.asBuffer()).toString();

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
