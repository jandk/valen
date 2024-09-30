package org.redeye.valen.game.spacemarines2.types.template;

import java.util.*;

public class ObjGeomVBufferMapping {
    public List<ObjGeomStreamToVBuffer> streamToVBuffer;

    public List<ObjGeomVBufferInfo> vBufferInfo;

    public void setvBufferInfo(List<ObjGeomVBufferInfo> vBufferInfo) {
        this.vBufferInfo = vBufferInfo;
    }

    public void setStreamToVBuffer(List<ObjGeomStreamToVBuffer> streamToVBuffer) {
        this.streamToVBuffer = streamToVBuffer;
    }
}
