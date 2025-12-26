package be.twofold.valen.game.idtech.decl.parser;

import wtf.reversed.toolbox.util.*;

public record DeclToken(
    DeclTokenType type,
    String value
) {
    public DeclToken {
        Check.nonNull(type, "type");
    }

    @Override
    public String toString() {
        return type + (value != null ? "('" + value + "')" : "");
    }
}
