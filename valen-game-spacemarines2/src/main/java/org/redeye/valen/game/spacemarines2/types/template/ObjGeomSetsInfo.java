package org.redeye.valen.game.spacemarines2.types.template;

import java.util.*;

public class ObjGeomSetsInfo {
    private List<ObjGeomStreamSetRef> streamRefSets;
    private List<ObjGeomStreamRef> streamRefs;
    private boolean streamingAvailable = false;

    public List<ObjGeomStreamSetRef> getStreamRefSets() {
        return streamRefSets;
    }

    public void setStreamRefSets(List<ObjGeomStreamSetRef> streamRefSets) {
        this.streamRefSets = streamRefSets;
    }

    public List<ObjGeomStreamRef> getStreamRefs() {
        return streamRefs;
    }

    public void setStreamRefs(List<ObjGeomStreamRef> streamRefs) {
        this.streamRefs = streamRefs;
    }

    public boolean getStreamingAvailable() {
        return streamingAvailable;
    }

    public void setStreamingAvailable(boolean streamingAvailable) {
        this.streamingAvailable = streamingAvailable;
    }
}
