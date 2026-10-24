package be.twofold.valen.game.idtech.reader.decl.renderparm;

import java.util.*;

public enum ParmScope {
    PSCP_CONST,
    PSCP_VIEW,
    PSCP_INSTANCE,
    PSCP_SURFACE,
    PSCP_NUM,
    ;

    public static Optional<ParmScope> parse(String parmScope) {
        return switch (parmScope.toLowerCase(Locale.ROOT)) {
            case "const" -> Optional.of(PSCP_CONST);
            case "view" -> Optional.of(PSCP_VIEW);
            case "instance" -> Optional.of(PSCP_INSTANCE);
            case "surface" -> Optional.of(PSCP_SURFACE);
            default -> Optional.empty();
        };
    }
}
