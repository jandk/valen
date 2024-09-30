package org.redeye.valen.game.spacemarines2.types.template;

import be.twofold.valen.core.math.*;

import java.util.*;

public class Split {
    public String name;
    public List<Short> vertRemap;
    public Matrix4 invMatrLT;


    public void setInvMatrLT(Matrix4 invMatrLT) {
        this.invMatrLT = invMatrLT;
    }

    public void setVertRemap(List<Short> vertRemap) {
        this.vertRemap = vertRemap;
    }

    public void setName(String name) {
        this.name = name;
    }
}
