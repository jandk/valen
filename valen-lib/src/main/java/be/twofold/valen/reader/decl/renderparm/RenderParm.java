package be.twofold.valen.reader.decl.renderparm;

import be.twofold.valen.reader.decl.renderparm.enums.*;
import be.twofold.valen.reader.image.*;

public final class RenderParm {

    public Object declaredValue;
    public RenderParmType parmType;
    public ParmEdit parmEdit;
    public ParmScope parmScope;
    public boolean cubeFilterTexture;
    public boolean streamed;
    public boolean globallyIndexed;
    public boolean editable;
    public boolean envNoInterpolation;
    public boolean fftBloom;
    public ImageTextureMaterialKind materialKind;
    public ImageTextureMaterialKind smoothnessNormalParm;

}
