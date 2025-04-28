package be.twofold.valen.game.eternal.reader.decl.renderparm;

import be.twofold.valen.game.eternal.defines.ParmScope;
import be.twofold.valen.game.eternal.defines.ParmType;
import be.twofold.valen.game.eternal.defines.TextureMaterialKind;
import be.twofold.valen.game.eternal.reader.decl.renderparm.enums.ParmEdit;

public final class RenderParm {

    public Object declaredValue;
    public ParmType parmType;
    public ParmEdit parmEdit;
    public ParmScope parmScope;
    public boolean cubeFilterTexture;
    public boolean streamed;
    public boolean globallyIndexed;
    public boolean editable;
    public boolean envNoInterpolation;
    public boolean fftBloom;
    public TextureMaterialKind materialKind;
    public TextureMaterialKind smoothnessNormalParm;

}
