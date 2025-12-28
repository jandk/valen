package be.twofold.valen.game.idtech.renderparm;

import be.twofold.valen.game.idtech.defines.*;

public final class RenderParm {

    public Object declaredValue;
    public ParmType parmType;
    public ParmEdit parmEdit;
    public ParmScope parmScope;
    public TextureMaterialKind materialKind;
    public TextureMaterialKind smoothnessNormalParm;

    // Flags
    // Eternal
    public boolean cubeFilterTexture;
    public boolean streamed;
    public boolean globallyIndexed;
    public boolean editable;
    public boolean envNoInterpolation;
    public boolean fftBloom;

    // Great Circle
    public boolean sfsFeedback;
    public boolean isTypeInfoShaderStruct;

    // Dark Ages
    public boolean materialFeedback;
    public boolean divergent;
    public boolean prefilterMips;
}
