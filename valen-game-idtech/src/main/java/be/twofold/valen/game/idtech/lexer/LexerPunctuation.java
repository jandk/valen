package be.twofold.valen.game.idtech.lexer;

public enum LexerPunctuation {
    P_RSHIFT_ASSIGN(">>="),
    P_LSHIFT_ASSIGN("<<="),
    P_PARMS("..."),
    P_PRECOMPMERGE("##"),
    P_LOGIC_AND("&&"),
    P_LOGIC_OR("||"),
    P_LOGIC_GEQ(">="),
    P_LOGIC_LEQ("<="),
    P_LOGIC_EQ("=="),
    P_LOGIC_UNEQ("!="),
    P_MUL_ASSIGN("*="),
    P_DIV_ASSIGN("/="),
    P_MOD_ASSIGN("%="),
    P_ADD_ASSIGN("+="),
    P_SUB_ASSIGN("-="),
    P_INC("++"),
    P_DEC("--"),
    P_BIN_AND_ASSIGN("&="),
    P_BIN_OR_ASSIGN("|="),
    P_BIN_XOR_ASSIGN("^="),
    P_RSHIFT(">>"),
    P_LSHIFT("<<"),
    P_SCOPE_RESOLUTION("::"),
    P_MEMBER_SELECTION_OBJECT("."),
    P_MEMBER_SELECTION_POINTER("->"),
    P_POINTER_TO_MEMBER_OBJECT(".*"),
    P_POINTER_TO_MEMBER_POINTER("->*"),
    P_MUL("*"),
    P_DIV("/"),
    P_MOD("%"),
    P_ADD("+"),
    P_SUB("-"),
    P_ASSIGN("="),
    P_BIN_AND("&"),
    P_BIN_OR("|"),
    P_BIN_XOR("^"),
    P_BIN_NOT("~"),
    P_LOGIC_NOT("!"),
    P_LOGIC_GREATER(">"),
    P_LOGIC_LESS("<"),
    P_COMMA(","),
    P_SEMICOLON(";"),
    P_COLON(":"),
    P_QUESTIONMARK("?"),
    P_PARENTHESESOPEN("("),
    P_PARENTHESESCLOSE(")"),
    P_BRACEOPEN("{"),
    P_BRACECLOSE("}"),
    P_SQBRACKETOPEN("["),
    P_SQBRACKETCLOSE("]"),
    P_BACKSLASH("\\"),
    P_PRECOMP("#"),
    P_DOLLAR("$"),
    P_XML_COMMENT("<!--"),
    P_APOSTROPHE("'"),
    P_QUOTE("\""),
    P_AT("@"),
    ;

    private final String text;

    LexerPunctuation(String text) {
        this.text = text;
    }

    String text() {
        return text;
    }
}
