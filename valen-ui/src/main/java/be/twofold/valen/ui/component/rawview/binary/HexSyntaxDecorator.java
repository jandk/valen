package be.twofold.valen.ui.component.rawview.binary;

import be.twofold.valen.ui.component.rawview.*;
import javafx.scene.paint.*;
import javafx.scene.text.*;
import jfx.incubator.scene.control.richtext.*;
import jfx.incubator.scene.control.richtext.model.*;
import wtf.reversed.toolbox.util.*;

import java.util.*;

final class HexSyntaxDecorator implements SyntaxDecorator {
    private final StyleAttributeMap[] byteStyles = new StyleAttributeMap[256];
    private final StyleAttributeMap[] charStyles = new StyleAttributeMap[256];
    private final StyleAttributeMap gutterStyle;

    private final HexDump hexDump;

    HexSyntaxDecorator(HexDump hexDump, Font font) {
        this.hexDump = Check.nonNull(hexDump, "hexDump");

        var styles = new HashMap<Color, StyleAttributeMap>();
        for (int value = 0; value < byteStyles.length; value++) {
            byteStyles[value] = styles.computeIfAbsent(HexColors.forByte(value), color -> RawCodeArea.style(font, color));
            charStyles[value] = styles.computeIfAbsent(HexColors.forChar(value), color -> RawCodeArea.style(font, color));
        }
        gutterStyle = styles.computeIfAbsent(HexColors.gutter(), color -> RawCodeArea.style(font, color));
    }

    @Override
    public RichParagraph createRichParagraph(CodeTextModel model, int index) {
        var row = new StyledRowSink();
        hexDump.row(index, row);
        return row.build();
    }

    @Override
    public void handleChange(CodeTextModel model, TextPos start, TextPos end, int charsTop, int linesAdded, int charsBottom) {
        // do nothing
    }

    private final class StyledRowSink implements HexDump.RowSink {
        private final RichParagraph.Builder builder = RichParagraph.builder();
        private final StringBuilder pending = new StringBuilder(HexDump.ROW_LENGTH);
        private StyleAttributeMap style = gutterStyle;

        @Override
        public void append(String text) {
            if (text.isBlank()) {
                // Blanks can just append without creating a new segment
                pending.append(text);
            } else {
                add(text, gutterStyle);
            }
        }

        @Override
        public void hex(int value) {
            add(HexDump.HEX[value], byteStyles[value]);
        }

        @Override
        public void ascii(int value) {
            add(HexDump.ALPHABET[value], charStyles[value]);
        }

        private void add(String text, StyleAttributeMap style) {
            if (!style.equals(this.style)) {
                flush();
                this.style = style;
            }
            pending.append(text);
        }

        private void flush() {
            if (!pending.isEmpty()) {
                builder.addSegment(pending.toString(), style);
                pending.setLength(0);
            }
        }

        RichParagraph build() {
            flush();
            return builder.build();
        }
    }
}
