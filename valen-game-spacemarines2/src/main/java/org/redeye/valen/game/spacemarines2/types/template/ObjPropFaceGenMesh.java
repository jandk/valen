package org.redeye.valen.game.spacemarines2.types.template;

import be.twofold.valen.core.math.*;

import java.util.*;

public class ObjPropFaceGenMesh implements ObjProp {
    public String controls;
    public String project;
    public String part;
    public Matrix4 invMatr;
    public List<Short> vertRemap;

    public void setControls(String controls) {
        this.controls = controls;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public void setPart(String part) {
        this.part = part;
    }

    public void setInvMatr(Matrix4 invMatr) {
        this.invMatr = invMatr;
    }

    public void setVertRemap(List<Short> vertRemap) {
        this.vertRemap = vertRemap;
    }

    @Override
    public String toString() {
        return "ObjPropFaceGenMesh{" +
            "controls='" + controls + '\'' +
            ", project='" + project + '\'' +
            ", part='" + part + '\'' +
            ", invMatr=" + invMatr +
            ", vertRemap=" + vertRemap +
            '}';
    }
}
