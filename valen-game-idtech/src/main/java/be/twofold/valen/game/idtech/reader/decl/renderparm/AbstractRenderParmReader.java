package be.twofold.valen.game.idtech.reader.decl.renderparm;

import be.twofold.valen.core.game.*;
import be.twofold.valen.game.idtech.lexer.*;
import be.twofold.valen.game.idtech.reader.image.*;
import wtf.reversed.toolbox.io.*;
import wtf.reversed.toolbox.util.*;

import java.io.*;
import java.util.*;
import java.util.function.*;

public abstract class AbstractRenderParmReader<V extends Asset> implements AssetReader.Binary<RenderParm, V> {
    private final Function<String, Optional<? extends MaterialKind>> materialKindParser;

    protected AbstractRenderParmReader(Function<String, Optional<? extends MaterialKind>> materialKindParser) {
        this.materialKindParser = Check.nonNull(materialKindParser, "materialKindParser");
    }

    protected RenderParm read(BinaryReader reader) throws IOException {
        var string = reader.readString(Math.toIntExact(reader.remaining()));
        var lexer = new IdLexer(string,
            LexerFlags.LEXFL_NOSTRINGCONCAT,
            LexerFlags.LEXFL_NOSTRINGESCAPECHARS,
            LexerFlags.LEXFL_ALLOWPATHNAMES,
            LexerFlags.LEXFL_ALLOWMULTICHARLITERALS,
            LexerFlags.LEXFL_ALLOWBACKSLASHSTRINGCONCAT,
            LexerFlags.LEXFL_ALLOWRAWSTRINGBLOCKS
        );
        return RenderParm.parse(lexer, materialKindParser);
    }
}
