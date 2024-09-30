package org.redeye.valen.game.spacemarines2.types.template;

import java.util.*;

public final class ActionFrame {
    Integer frame;
    String comment;
    List<String> flags;

    public ActionFrame(
        Integer frame,
        String comment,
        List<String> flags
    ) {
        this.frame = frame;
        this.comment = comment;
        this.flags = flags;
    }

    public ActionFrame() {
        this(null, null, null);
    }

    public void setFrame(Integer v) {
        frame = v;
    }

    public void setComment(String v) {
        comment = v;
    }

    public void setFlags(List<String> v) {
        flags = v;
    }

    @Override
    public String toString() {
        return "ActionFrame[" +
            "frame=" + frame + ", " +
            "comment=" + comment + ", " +
            "flags=" + flags + ']';
    }

}
