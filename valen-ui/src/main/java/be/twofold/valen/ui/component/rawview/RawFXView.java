package be.twofold.valen.ui.component.rawview;

import be.twofold.valen.core.util.*;
import be.twofold.valen.ui.common.*;
import jakarta.inject.*;
import javafx.collections.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.text.*;

import java.util.*;
import java.util.stream.*;

public final class RawFXView implements RawView, FXView {
    private static final Font MONOSPACED =
        Stream.of("Jetbrains Mono", "Cascadia Mono", "Consolas", "Monospaced")
            .map(Font::font)
            .filter(font -> !Font.getDefault().equals(font))
            .findFirst().orElseThrow();

    private final VBox view = new VBox();
    private final ListView<Integer> binaryView = new ListView<>();
    private final TextArea textView = new TextArea();

    private HexDump hexDump;

    @Inject
    public RawFXView() {
        setBinary(new byte[0]);

        VBox.setVgrow(binaryView, Priority.ALWAYS);
        VBox.setVgrow(textView, Priority.ALWAYS);

        // Disable selection
        // binaryView.addEventFilter(MouseEvent.MOUSE_PRESSED, Event::consume);
        binaryView.setCellFactory(_ -> new HexdumpListCell());
        binaryView.setFixedCellSize(20);
        binaryView.setFocusModel(null);
        binaryView.setSelectionModel(null);

        // Disable editing
        textView.setEditable(false);
        textView.setFont(MONOSPACED);

        // Little hack, but 8 is too much
        // TODO: Find a better solution
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

        // TODO: Choose themed color
        hexDump = new HexDump(binary, MONOSPACED, Color.WHITE, Color.GRAY);
        int numRows = (binary.length + 15) / 16;
        binaryView.scrollTo(0);
        binaryView.setItems(new IndexObservableList(numRows));
    }

    @Override
    public void setText(String text) {
        hexDump = null;
        binaryView.setItems(FXCollections.emptyObservableList());
        view.getChildren().setAll(textView);
        textView.setText(text);
    }

    private final class HexdumpListCell extends ListCell<Integer> {
        @Override
        protected void updateItem(Integer item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setGraphic(null);
            } else {
                setGraphic(hexDump.hexdump(item));
            }
            setFont(MONOSPACED);
            setText(null);
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
