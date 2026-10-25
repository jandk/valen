package be.twofold.valen.ui.component.rawview.binary;

import jfx.incubator.scene.control.richtext.*;
import jfx.incubator.scene.control.richtext.model.*;
import wtf.reversed.toolbox.util.*;

final class BinaryContent implements BasicTextModel.Content {
    private final HexDump hexDump;

    BinaryContent(HexDump hexDump) {
        this.hexDump = Check.nonNull(hexDump, "hexDump");
    }

    @Override
    public int size() {
        return hexDump.size();
    }

    @Override
    public String getText(int index) {
        var builder = new StringBuilder(HexDump.ROW_LENGTH);
        hexDump.row(index, builder::append);
        return builder.toString();
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
