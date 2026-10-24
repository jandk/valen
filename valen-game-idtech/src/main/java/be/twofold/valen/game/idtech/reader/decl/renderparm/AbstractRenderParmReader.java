package be.twofold.valen.game.idtech.reader.decl.renderparm;

import be.twofold.valen.core.game.*;
import be.twofold.valen.game.idtech.lexer.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;

public abstract class AbstractRenderParmReader<V extends Asset> implements AssetReader.Binary<RenderParm, V> {
    protected RenderParm read(BinarySource source) throws IOException {
        var string = source.readString(Math.toIntExact(source.size()));
        var lexer = new IdLexer(string,
            LexerFlags.LEXFL_NOSTRINGCONCAT,
            LexerFlags.LEXFL_NOSTRINGESCAPECHARS,
            LexerFlags.LEXFL_ALLOWPATHNAMES,
            LexerFlags.LEXFL_ALLOWMULTICHARLITERALS,
            LexerFlags.LEXFL_ALLOWBACKSLASHSTRINGCONCAT,
            LexerFlags.LEXFL_ALLOWRAWSTRINGBLOCKS
        );
        return RenderParm.parse(lexer);
    }
}
