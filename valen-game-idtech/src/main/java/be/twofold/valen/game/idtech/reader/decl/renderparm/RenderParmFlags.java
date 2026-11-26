package be.twofold.valen.game.idtech.reader.decl.renderparm;

import be.twofold.valen.game.idtech.lexer.*;

import java.util.*;

public enum RenderParmFlags {
    // DOOM (2016)
    SET_FREQ_LOW,
    CUBE_FILTER_TEXTURE,
    DYNAMIC_OFFSET,
    // DOOM Eternal
    STREAMED,
    GLOBALLY_INDEXED,
    EDITABLE,
    ENV_NO_INTERPOLATION,
    FFT_BLOOM,
    // Indiana Jones and the Great Circle
    SFS_FEEDBACK,
    // DOOM: The Dark Ages
    MATERIAL_FEEDBACK,
    DIVERGENT,
    PREFILTER_MIPS,
    ;

    static RenderParmFlags parse(IdLexer lexer) {
        var flag = switch (RenderParm.peekValue(lexer).toLowerCase(Locale.ROOT)) {
            // DOOM (2016)
            case "setfreqlow" -> SET_FREQ_LOW;
            case "dynamicoffset" -> DYNAMIC_OFFSET;
            // DOOM Eternal
            case "streamed" -> STREAMED;
            case "globallyindexed" -> GLOBALLY_INDEXED;
            case "material", "edit" -> EDITABLE;
            case "env_nointerp" -> ENV_NO_INTERPOLATION;
            case "fftbloom" -> FFT_BLOOM;
            // Indiana Jones and the Great Circle
            case "sfsfeedback" -> SFS_FEEDBACK;
            // DOOM: The Dark Ages
            case "materialfeedback" -> MATERIAL_FEEDBACK;
            case "divergent" -> DIVERGENT;
            case "prefiltermips" -> PREFILTER_MIPS;
            default -> null;
        };
        if (flag != null) {
            lexer.expectName();
        }
        return flag;
    }
}
