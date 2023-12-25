package be.twofold.valen.reader.decl.parser;

public enum DeclTokenType {
    // Punctuation
    LeftShift,
    Assign,
    Comma,
    Semicolon,
    OpenParen,
    CloseParen,
    OpenBrace,
    CloseBrace,
    OpenBracket,
    CloseBracket,
    Dollar,

    // Values
    String,
    Number,
    Name,

    // End of file
    Eof,
}
