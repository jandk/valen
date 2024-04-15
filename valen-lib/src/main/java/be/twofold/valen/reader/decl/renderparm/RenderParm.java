package be.twofold.valen.reader.decl.renderparm;

import be.twofold.valen.reader.decl.renderparm.enums.*;
import be.twofold.valen.reader.image.*;

public final class RenderParm {

    Object declaredValue;
    RenderParmType parmType;
    ParmEdit parmEdit;
    ParmScope parmScope;
    boolean cubeFilterTexture;
    boolean streamed;
    boolean globallyIndexed;
    boolean editable;
    boolean envNoInterpolation;
    boolean fftBloom;
    ImageTextureMaterialKind materialKind;
    String smoothnessNormalParm;

}
