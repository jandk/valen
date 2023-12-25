package be.twofold.valen.reader.decl.parser;

import be.twofold.valen.core.util.*;

public record DeclToken(
    DeclTokenType type,
    String value
) {
    public DeclToken {
        Check.notNull(type);
    }

    @Override
    public String toString() {
        return type + (value != null ? "('" + value + "')" : "");
    }
}
