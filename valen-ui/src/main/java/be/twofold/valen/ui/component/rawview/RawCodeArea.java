package be.twofold.valen.ui.component.rawview;

import javafx.scene.paint.*;
import javafx.scene.text.*;
import jfx.incubator.scene.control.richtext.*;
import jfx.incubator.scene.control.richtext.model.*;

import java.util.stream.*;

public final class RawCodeArea extends CodeArea {
    public static final Font MONOSPACED =
        Stream.of("Jetbrains Mono", "Cascadia Mono", "Consolas", "Monospaced")
            .map(Font::font)
            .filter(font -> !Font.getDefault().equals(font))
            .findFirst().orElseThrow();

    public RawCodeArea() {
        setEditable(false);
        setFont(MONOSPACED);
        setTabSize(4);
        setWrapText(false);
    }

    public static StyleAttributeMap style(Font font, Color color) {
        return StyleAttributeMap.builder()
            .setFontFamily(font.getFamily())
            .setFontSize(font.getSize())
            .setTextColor(color)
            .build();
    }
}
