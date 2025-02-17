package be.twofold.valen.ui.component.rawview;

import be.twofold.valen.core.util.*;
import be.twofold.valen.ui.common.*;
import jakarta.inject.*;
import javafx.collections.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;

import java.nio.charset.*;
import java.util.*;

public final class RawFXView implements RawView, FXView {
    private static final List<String> FONTS = List.of(
        "Jetbrains Mono",
        "Cascadia Mono",
        "Consolas",
        "Monospaced"
    );
    private static final Font MONOSPACED;

    static {
        MONOSPACED = FONTS.stream()
            .map(Font::font)
            .filter(font -> !Font.getDefault().equals(font))
            .findFirst().orElseThrow();
    }

    private final VBox view = new VBox();
    private final ListView<Integer> binaryView = new ListView<>();
    private final TextArea textView = new TextArea();

    private byte[] binary;

    @Inject
    public RawFXView() {
        setBinary(new byte[0]);

        VBox.setVgrow(binaryView, Priority.ALWAYS);
        VBox.setVgrow(textView, Priority.ALWAYS);

        // Disable selection
        // binaryView.addEventFilter(MouseEvent.MOUSE_PRESSED, Event::consume);
        binaryView.setCellFactory(_ -> new HexdumpListCell());
        binaryView.setFixedCellSize(20);

        // Disable editing
        textView.setEditable(false);
        textView.setFont(MONOSPACED);

        // Little hack, but 8 is too much
        var styleText = "Text { -fx-tab-size: 4; }";
        var styleBase64 = Base64.getUrlEncoder().encodeToString(styleText.getBytes());
        var url = "data:text/css;charset=UTF-8;base64," + styleBase64;
        view.getStylesheets().add(url);
    }

    @Override
    public Parent getFXNode() {
        return view;
    }

    @Override
    public void setBinary(byte[] binary) {
        textView.clear();
        view.getChildren().setAll(binaryView);

        this.binary = binary;
        int numRows = (binary.length + 15) / 16;
        binaryView.scrollTo(0);
        binaryView.setItems(new IndexObservableList(numRows));
    }

    @Override
    public void setText(String text) {
        binary = null;
        binaryView.setItems(FXCollections.emptyObservableList());
        view.getChildren().setAll(textView);
        textView.setText(text);
    }

    private final class HexdumpListCell extends ListCell<Integer> {
        private static final char[] HEX = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
        };
        private static final char[] CHARS;

        static {
            var bytes = new byte[256];
            for (int i = 0; i < bytes.length; i++) {
                bytes[i] = (byte) i;
            }

            CHARS = new char[256];
            var charset = new String(bytes, Charset.forName("windows-1252")).toCharArray();
            for (int i = 0; i < charset.length; i++) {
                var ch = charset[i];
                CHARS[i] = Character.getType(ch) != Character.CONTROL && ch != 0xFFFD ? ch : '.';
            }
        }

        @Override
        protected void updateItem(Integer item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setText(null);
            } else {
                setText(hexdump(item));
            }
            setFont(MONOSPACED);
        }

        private String hexdump(int index) {
            var chars = new char[78];
            Arrays.fill(chars, ' ');

            int offset = index * 16;
            toHex(chars, 0, offset, 8);

            dumpBytes(offset, chars, 10);
            dumpBytes(offset + 8, chars, 35);
            dumpAscii(offset, chars, 60);

            return new String(chars);
        }

        private void dumpBytes(int offset, char[] chars, int charOffset) {
            for (int i = 0; i < 8; i++) {
                if (offset + i >= binary.length) {
                    break;
                }
                int value = Byte.toUnsignedInt(binary[offset + i]);
                toHex(chars, charOffset + i * 3, value, 2);
            }
        }

        private void dumpAscii(int offset, char[] chars, int charOffset) {
            chars[charOffset++] = '|';
            for (int i = 0; i < 16; i++) {
                if (offset + i >= binary.length) {
                    break;
                }
                int value = Byte.toUnsignedInt(binary[offset + i]);
                // char print = isPrintable(value) ? (char) value : '.';
                char print = CHARS[value];
                chars[charOffset++] = print;
            }
            chars[charOffset] = '|';
        }

        private static boolean isPrintable(int value) {
            return (value > 0x1F && value < 0x7F) || value > 0x9F;
        }

        private void toHex(char[] chars, int offset, int value, int length) {
            for (int i = length - 1, o = offset; i >= 0; i--, o++) {
                chars[o] = HEX[(value >> (i * 4)) & 0xF];
            }
        }
    }

    private static final class IndexObservableList extends ObservableListBase<Integer> {
        private final int size;

        public IndexObservableList(int size) {
            Check.argument(size >= 0);
            this.size = size;
        }

        @Override
        public Integer get(int index) {
            return Check.index(index, size);
        }

        @Override
        public int size() {
            return size;
        }
    }
}
