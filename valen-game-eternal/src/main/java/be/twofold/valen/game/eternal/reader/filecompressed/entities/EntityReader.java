package be.twofold.valen.game.eternal.reader.filecompressed.entities;

import be.twofold.valen.core.game.*;
import be.twofold.valen.game.eternal.*;
import be.twofold.valen.game.eternal.reader.filecompressed.*;
import be.twofold.valen.game.idtech.game.*;
import be.twofold.valen.game.idtech.lexer.*;
import be.twofold.valen.game.idtech.reader.decl.parser.*;
import com.google.gson.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.nio.charset.*;
import java.util.*;

public final class EntityReader implements AssetReader.Binary<EntityFile, EternalAsset> {
    private final FileCompressedReader fileCompressedReader;

    public EntityReader(FileCompressedReader fileCompressedReader) {
        this.fileCompressedReader = fileCompressedReader;
    }

    @Override
    public boolean canRead(EternalAsset asset) {
        return asset.id().type() == ResourceType.of("compfile")
            && asset.id().path().extension().equals("entities");
    }

    @Override
    public EntityFile read(BinarySource source, EternalAsset asset, LoadingContext context) throws IOException {
        var bytes = fileCompressedReader.read(source, asset, context);
        var input = StandardCharsets.UTF_8.decode(bytes.asBuffer()).toString();

        var lexer = DeclParser.createLexer(input);
        var parser = new DeclParser(lexer);

        var version = lexer.checkTokenString("Version")
            ? lexer.parseInt()
            : 0;
        var hierarchyVersion = lexer.checkTokenString("HierarchyVersion")
            ? lexer.parseInt()
            : 0;
        if (lexer.checkTokenString("properties")) {
            skipProperties(lexer);
        }

        var entities = new LinkedHashMap<String, Entity>();
        while (lexer.checkTokenString("entity")) {
            lexer.expectPunctuation(LexerPunctuation.P_BRACEOPEN);

            // The engine accepts these in any order
            var layers = new ArrayList<String>();
            Integer instanceId = null;
            String originalName = null;
            String name = null;
            JsonObject entityDef = null;
            while (!lexer.checkPunctuation(LexerPunctuation.P_BRACECLOSE)) {
                var keyword = lexer.expectName().value();
                switch (keyword) {
                    case "layers" -> {
                        lexer.expectPunctuation(LexerPunctuation.P_BRACEOPEN);
                        while (!lexer.checkPunctuation(LexerPunctuation.P_BRACECLOSE)) {
                            layers.add(lexer.expectTokenType(TokenType.TT_STRING, 0).value());
                        }
                    }
                    case "instanceId" -> {
                        lexer.expectPunctuation(LexerPunctuation.P_ASSIGN);
                        instanceId = lexer.parseInt();
                        lexer.expectPunctuation(LexerPunctuation.P_SEMICOLON);
                    }
                    case "originalName" -> {
                        lexer.expectPunctuation(LexerPunctuation.P_ASSIGN);
                        originalName = lexer.expectTokenType(TokenType.TT_STRING, 0).value();
                        lexer.expectPunctuation(LexerPunctuation.P_SEMICOLON);
                    }
                    case "entityDef" -> {
                        name = lexer.expectName().value();
                        lexer.expectPunctuation(LexerPunctuation.P_BRACEOPEN);
                        entityDef = parser.parseObject();
                    }
                    default -> throw new DeclParseException("Unknown entity keyword '" + keyword + "'");
                }
            }

            if (name == null) {
                throw new DeclParseException("Entity without entityDef");
            }
            entities.put(name, new Entity(layers, instanceId, originalName, entityDef));
        }

        var token = lexer.readToken();
        if (token != null) {
            throw new DeclParseException("Expected entity, but found '" + token.value() + "'");
        }
        return new EntityFile(version, hierarchyVersion, entities);
    }

    private static void skipProperties(IdLexer lexer) {
        lexer.expectPunctuation(LexerPunctuation.P_BRACEOPEN);
        while (!lexer.checkPunctuation(LexerPunctuation.P_BRACECLOSE)) {
            lexer.expectAnyToken();
            lexer.expectPunctuation(LexerPunctuation.P_ASSIGN);
            lexer.expectAnyToken();
        }
    }
}
