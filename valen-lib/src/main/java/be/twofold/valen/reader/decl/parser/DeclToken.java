package be.twofold.valen.reader.decl.parser;

public record DeclToken(
    DeclTokenType type,
    String value
) {
    @Override
    public String toString() {
        return type + (value != null ? "('" + value + "')" : "");
    }
}
