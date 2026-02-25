package be.twofold.valen.ui.component.rawview;

import be.twofold.valen.ui.common.*;
import jakarta.inject.*;
import javafx.collections.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.text.*;
import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.util.*;

import java.util.*;
import java.util.stream.*;

public final class RawViewImpl extends AbstractView<View.Listener> implements RawView {
    private static final Font MONOSPACED =
        Stream.of("Jetbrains Mono", "Cascadia Mono", "Consolas", "Monospaced")
            .map(Font::font)
            .filter(font -> !Font.getDefault().equals(font))
            .findFirst().orElseThrow();

    private final VBox view = new VBox();
    private final ListView<Integer> binaryView = new ListView<>();
    private final ListView<Integer> textView = new ListView<>();

    private HexDump hexDump;
    private Lines lines;

    @Inject
    public RawViewImpl() {
        setupListView(binaryView);
        binaryView.setCellFactory(_ -> new BinaryListCell());

        setupListView(textView);
        textView.setCellFactory(_ -> new TextListCell());

        // Little hack, but 8 is too much
        // TODO: Find a better solution
        var styleText = "Text { -fx-tab-size: 4; }";
        var styleBase64 = Base64.getUrlEncoder().encodeToString(styleText.getBytes());
        var url = "data:text/css;charset=UTF-8;base64," + styleBase64;
        view.getStylesheets().add(url);
    }

    private void setupListView(ListView<Integer> listView) {
        VBox.setVgrow(listView, Priority.ALWAYS);
        listView.setFixedCellSize(20);
        listView.setFocusModel(null);
        listView.setSelectionModel(null);
    }

    @Override
    public Parent getFXNode() {
        return view;
    }

    @Override
    public void setBinary(Bytes bytes) {
        // TODO: Choose themed color
        hexDump = new HexDump(bytes, MONOSPACED, Color.WHITE, Color.GRAY);
        lines = null;

        binaryView.setItems(new IndexObservableList((bytes.length() + 15) / 16));
        textView.setItems(FXCollections.emptyObservableList());
        view.getChildren().setAll(binaryView);
    }

    @Override
    public void setText(String text) {
        hexDump = null;
        lines = Lines.parse(text);

        binaryView.setItems(FXCollections.emptyObservableList());
        textView.setItems(new IndexObservableList(lines.size()));
        view.getChildren().setAll(textView);
    }

    @Override
    public void clear() {
        binaryView.setItems(FXCollections.emptyObservableList());
        textView.setItems(FXCollections.emptyObservableList());

        hexDump = null;
        lines = null;
    }

    private final class BinaryListCell extends ListCell<Integer> {
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

    private final class TextListCell extends ListCell<Integer> {
        @Override
        protected void updateItem(Integer item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setText(null);
            } else {
                if (item < lines.size()) {
                    setText(lines.get(item).replace("\t", "    "));
                } else {
                    setText("");
                }
            }
            setFont(MONOSPACED);
            setGraphic(null);
        }
    }

    private static final class IndexObservableList extends ObservableListBase<Integer> {
        private final int size;

        public IndexObservableList(int size) {
            Check.positiveOrZero(size, "size");
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
