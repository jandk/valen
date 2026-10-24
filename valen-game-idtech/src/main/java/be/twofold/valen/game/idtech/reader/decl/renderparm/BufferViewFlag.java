package be.twofold.valen.game.idtech.reader.decl.renderparm;

import java.util.*;

public enum BufferViewFlag {
    BVF_WRITABLE,
    BVF_COHERENT,
    BVF_WRITEONLY,
    // Dark Ages
    BVF_DEBUG_SKIP_OOB_CHECKS,
    ;

    public static Optional<BufferViewFlag> parse(String flag) {
        return switch (flag.toLowerCase(Locale.ROOT)) {
            case "writable" -> Optional.of(BVF_WRITABLE);
            case "coherent" -> Optional.of(BVF_COHERENT);
            case "skipoobchecks" -> Optional.of(BVF_DEBUG_SKIP_OOB_CHECKS);
            default -> Optional.empty();
        };
    }
}
