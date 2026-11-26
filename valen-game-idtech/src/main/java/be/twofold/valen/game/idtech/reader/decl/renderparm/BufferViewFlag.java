package be.twofold.valen.game.idtech.reader.decl.renderparm;

import java.util.*;

public enum BufferViewFlag {
    // DOOM (2016)
    BVF_WRITABLE,
    BVF_WRITABLE_CS,
    BVF_COHERENT,
    BVF_EARLYDEPTHSTENCIL,
    // DOOM Eternal
    BVF_WRITEONLY,
    // DOOM: The Dark Ages
    BVF_DEBUG_SKIP_OOB_CHECKS,
    ;

    public static Optional<BufferViewFlag> parse(String flag) {
        return switch (flag.toLowerCase(Locale.ROOT)) {
            case "writable" -> Optional.of(BVF_WRITABLE);
            case "writable_cs" -> Optional.of(BVF_WRITABLE_CS);
            case "coherent" -> Optional.of(BVF_COHERENT);
            case "early_fragment_tests" -> Optional.of(BVF_EARLYDEPTHSTENCIL);
            case "skipoobchecks" -> Optional.of(BVF_DEBUG_SKIP_OOB_CHECKS);
            default -> Optional.empty();
        };
    }
}
