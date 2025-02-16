package be.twofold.valen.ui.component.rawview;

import be.twofold.valen.ui.common.*;
import jakarta.inject.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;

import java.nio.charset.*;
import java.util.*;
import java.util.stream.*;

public final class RawFXView implements RawView, FXView {
    private static final Font MONOSPACE = Font.font("Cascadia Mono", Font.getDefault().getSize());

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
        textView.setFont(MONOSPACE);

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
        var items = IntStream.range(0, numRows).boxed().toList();
        binaryView.getItems().setAll(items);
    }

    @Override
    public void setText(String text) {
        binary = null;
        binaryView.getItems().clear();
        view.getChildren().setAll(textView);
        textView.setText(text);
    }

    private final class HexdumpListCell extends ListCell<Integer> {
        private static final byte[] HEX = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
        };

        @Override
        protected void updateItem(Integer item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setText(null);
            } else {
                setText(hexdump(item));
            }
            setFont(MONOSPACE);
        }

        private String hexdump(int index) {
            var chars = new byte[78];
            Arrays.fill(chars, (byte) 0x20);

            int offset = index * 16;
            toHex(chars, 0, offset, 8);

            dumpBytes(offset, chars, 10);
            dumpBytes(offset + 8, chars, 35);
            dumpAscii(offset, chars, 60);

            return new String(chars, StandardCharsets.ISO_8859_1);
        }

        private void dumpBytes(int offset, byte[] chars, int charOffset) {
            for (int i = 0; i < 8; i++) {
                if (offset + i >= binary.length) {
                    break;
                }
                int value = Byte.toUnsignedInt(binary[offset + i]);
                toHex(chars, charOffset + i * 3, value, 2);
            }
        }

        private void dumpAscii(int offset, byte[] chars, int charOffset) {
            chars[charOffset++] = '|';
            for (int i = 0; i < 16; i++) {
                if (offset + i >= binary.length) {
                    break;
                }
                int value = Byte.toUnsignedInt(binary[offset + i]);
                byte print = isPrintable(value) ? (byte) value : (byte) '.';
                chars[charOffset++] = print;
            }
            chars[charOffset] = '|';
        }

        private static boolean isPrintable(int value) {
            return (value > 0x1F && value < 0x7F) || value > 0x9F;
        }

        private void toHex(byte[] target, int offset, int value, int length) {
            for (int i = length - 1, o = offset; i >= 0; i--, o++) {
                target[o] = HEX[(value >> (i * 4)) & 0xF];
            }
        }
    }
}
