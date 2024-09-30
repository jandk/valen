package org.redeye.valen.game.spacemarines2.types.template;

import org.redeye.valen.game.spacemarines2.types.*;

import java.util.*;

public final class AnimSeq {
    String name;
    Integer layerId;
    Float startFrame;
    Float endFrame;
    Float offsetFrame;
    Float lenFrame;
    Float timeSec;
    List<ActionFrame> actionFrames;
    BBox bbox;

    public AnimSeq(
        String name,
        Integer layerId,
        Float startFrame,
        Float endFrame,
        Float offsetFrame,
        Float lenFrame,
        Float timeSec,
        List<ActionFrame> actionFrames,
        BBox bbox
    ) {
        this.name = name;
        this.layerId = layerId;
        this.startFrame = startFrame;
        this.endFrame = endFrame;
        this.offsetFrame = offsetFrame;
        this.lenFrame = lenFrame;
        this.timeSec = timeSec;
        this.actionFrames = actionFrames;
        this.bbox = bbox;
    }

    public AnimSeq() {
        this(null, null, null, null, null, null, null, null, null);
    }


    @Override
    public String toString() {
        return "AnimSeq[" +
            "name=" + name + ", " +
            "layerId=" + layerId + ", " +
            "startFrame=" + startFrame + ", " +
            "endFrame=" + endFrame + ", " +
            "offsetFrame=" + offsetFrame + ", " +
            "lenFrame=" + lenFrame + ", " +
            "timeSec=" + timeSec + ", " +
            "actionFrames=" + actionFrames + ", " +
            "bbox=" + bbox + ']';
    }

    public void setName(String v) {
        name = v;
    }

    public void setLayerId(Integer v) {
        layerId = v;
    }

    public void setStartFrame(Float v) {
        startFrame = v;
    }

    public void setEndFrame(Float v) {
        endFrame = v;
    }

    public void setOffsetFrame(Float v) {
        offsetFrame = v;
    }

    public void setLenFrame(Float v) {
        lenFrame = v;
    }

    public void setTimeSec(Float v) {
        timeSec = v;
    }

    public void setActionFrames(List<ActionFrame> v) {
        actionFrames = v;
    }

    public void setBbox(BBox v) {
        bbox = v;
    }
}
