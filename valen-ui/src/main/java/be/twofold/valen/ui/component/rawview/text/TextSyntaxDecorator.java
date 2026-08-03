package be.twofold.valen.ui.component.rawview.text;

import be.twofold.valen.ui.component.rawview.*;
import javafx.scene.paint.*;
import javafx.scene.text.*;
import jfx.incubator.scene.control.richtext.*;
import jfx.incubator.scene.control.richtext.model.*;

final class TextSyntaxDecorator implements SyntaxDecorator {
    // Same as -fx-light-text-color in style.css
    private static final Color TEXT = Color.rgb(220, 220, 220);

    private final StyleAttributeMap style;

    TextSyntaxDecorator(Font font) {
        style = RawCodeArea.style(font, TEXT);
    }

    @Override
    public RichParagraph createRichParagraph(CodeTextModel model, int index) {
        return RichParagraph.builder()
            .addSegment(model.getPlainText(index), style)
            .build();
    }

    @Override
    public void handleChange(CodeTextModel m, TextPos start, TextPos end, int charsTop, int linesAdded, int charsBottom) {
        // do nothing
    }
}
