package be.twofold.valen.ui.component.rawview.binary;

import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.text.*;
import jfx.incubator.scene.control.richtext.*;
import wtf.reversed.toolbox.util.*;

final class HexOffsetDecorator implements SideDecorator {
    private final HexDump hexDump;
    private final Font font;

    HexOffsetDecorator(HexDump hexDump, Font font) {
        this.hexDump = Check.nonNull(hexDump, "hexDump");
        this.font = Check.nonNull(font, "font");
    }

    @Override
    public double getPrefWidth(double viewWidth) {
        return 0;
    }

    @Override
    public Node getMeasurementNode(int index) {
        return createNode(" 0000:0000 ");
    }

    @Override
    public Node getNode(int index) {
        return createNode(hexDump.offset(index));
    }

    private Node createNode(String text) {
        var node = new Text(text);
        node.setFont(font);
        node.setFill(HexColors.gutter());
        node.setTextOrigin(VPos.TOP);
        return node;
    }
}
