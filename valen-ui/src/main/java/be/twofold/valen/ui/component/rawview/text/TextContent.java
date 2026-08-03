package be.twofold.valen.ui.component.rawview.text;

import jfx.incubator.scene.control.richtext.*;
import jfx.incubator.scene.control.richtext.model.*;
import wtf.reversed.toolbox.util.*;

final class TextContent implements BasicTextModel.Content {
    private final Lines lines;

    TextContent(Lines lines) {
        this.lines = Check.nonNull(lines, "lines");
    }

    @Override
    public int size() {
        return lines.size();
    }

    @Override
    public String getText(int index) {
        return lines.get(index);
    }

    @Override
    public int insertTextSegment(int index, int offset, String text, StyleAttributeMap attrs) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void insertLineBreak(int index, int offset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeRange(TextPos start, TextPos end) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isWritable() {
        return false;
    }
}
