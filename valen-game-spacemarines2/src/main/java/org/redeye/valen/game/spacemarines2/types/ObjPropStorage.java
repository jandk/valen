package org.redeye.valen.game.spacemarines2.types;

import java.util.*;

public class ObjPropStorage {
    public ObjProp[] objProps;

    public void setObjProps(ObjProp[] objProps) {
        this.objProps = objProps;
    }

    @Override
    public String toString() {
        return "ObjPropStorage{" +
            "objProps=" + Arrays.toString(objProps) +
            '}';
    }
}
