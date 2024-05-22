package be.twofold.valen.reader.filecompressed.entities;

import be.twofold.valen.core.io.*;
import be.twofold.valen.manager.*;
import be.twofold.valen.reader.*;
import be.twofold.valen.reader.decl.*;
import be.twofold.valen.reader.decl.parser.*;
import be.twofold.valen.reader.filecompressed.*;
import be.twofold.valen.resource.*;
import com.google.gson.*;
import jakarta.inject.*;

import java.io.*;
import java.nio.charset.*;
import java.util.*;

public final class EntityReader implements ResourceReader<EntityFile> {
    private final Provider<FileManager> fileManagerProvider;
    private final FileCompressedReader fileCompressedReader;

    @Inject
    public EntityReader(
        Provider<FileManager> fileManagerProvider,
        FileCompressedReader fileCompressedReader
    ) {
        this.fileManagerProvider = fileManagerProvider;
        this.fileCompressedReader = fileCompressedReader;
    }

    @Override
    public boolean canRead(Resource entry) {
        return entry.type() == ResourceType.CompFile
               && entry.name().extension().equals("entities");
    }

    @Override
    public EntityFile read(DataSource source, Resource resource) throws IOException {
        var parentCache = new HashMap<String, JsonObject>();
        byte[] bytes = fileCompressedReader.read(source, resource);
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
                while (!parser.match(DeclTokenType.CloseBrace)) {
                    layers.add(parser.expectString());
                }
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

            if (entityDef.has("inherit")) {
                var parentName = entityDef.get("inherit").getAsString();
                var inhObject = parentCache.computeIfAbsent(parentName, s -> fileManagerProvider.get()
                    .readResource(FileType.Declaration, "generated/decls/entitydef/" + s + ".decl"));
                entityDef = DeclReader.merge(inhObject, entityDef);
            }

            var entity = new Entity(layers, instanceId, originalName, entityDef);
            entities.put(name, entity);

            parser.expect(DeclTokenType.CloseBrace);
        }

        return new EntityFile(version, hierarchyVersion, entities);
    }
}
