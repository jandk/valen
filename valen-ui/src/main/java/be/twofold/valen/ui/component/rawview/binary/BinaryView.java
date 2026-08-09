package be.twofold.valen.ui.component.rawview.binary;

import be.twofold.valen.ui.component.rawview.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import jfx.incubator.scene.control.richtext.model.*;
import wtf.reversed.toolbox.collect.*;

public final class BinaryView {
    private final RawCodeArea codeArea = new RawCodeArea();

    private HexDump hexDump;

    public BinaryView() {
        buildUI();
        clear();
    }

    public Parent getFXNode() {
        return codeArea;
    }

    public void setBinary(Bytes binary) {
        this.hexDump = new HexDump(binary);

        var model = new CodeTextModel(new BinaryContent(hexDump));
        model.setDecorator(new HexSyntaxDecorator(hexDump, RawCodeArea.MONOSPACED));
        codeArea.setModel(model);

        codeArea.setLeftDecorator(new HexOffsetDecorator(hexDump, RawCodeArea.MONOSPACED));
    }

    public void clear() {
        setBinary(Bytes.empty());
    }

    private void copyBytes(boolean asHex) {
        var selection = codeArea.getSelection();
        if (selection == null || selection.isCollapsed()) {
            return;
        }

        var min = selection.getMin();
        var max = selection.getMax();
        int lo = hexDump.byteIndex(min.index(), min.offset(), false);
        int hi = hexDump.byteIndex(max.index(), max.offset(), true);
        if (hi <= lo) {
            return;
        }

        var content = new ClipboardContent();
        content.putString(asHex ? hexDump.hex(lo, hi) : hexDump.raw(lo, hi));
        Clipboard.getSystemClipboard().setContent(content);
    }

    // region UI

    private void buildUI() {
        codeArea.setContextMenu(buildContextMenu());
        codeArea.setLineNumbersEnabled(false);

        // codeArea.getInputMap().register(KeyBinding.shiftShortcut(KeyCode.C), () -> copyBytes(true));
    }

    private ContextMenu buildContextMenu() {
        var copyItem = new MenuItem("Copy");
        copyItem.setOnAction(_ -> codeArea.copy());

        var copyHexItem = new MenuItem("Copy Hex");
        copyHexItem.setOnAction(_ -> copyBytes(true));

        var copyTextItem = new MenuItem("Copy Text");
        copyTextItem.setOnAction(_ -> copyBytes(false));

        return new ContextMenu(copyItem/*, copyHexItem, copyTextItem*/);
    }

    // endregion

}
