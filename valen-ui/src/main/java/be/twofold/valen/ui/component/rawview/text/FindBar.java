package be.twofold.valen.ui.component.rawview.text;

import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import wtf.reversed.toolbox.util.*;

final class FindBar extends HBox {
    private static final String ERROR_CLASS = "find-error";

    private final TextField field = new TextField();
    private final Searcher searcher;
    private final Runnable onClose;

    FindBar(Searcher searcher, Runnable onClose) {
        this.searcher = Check.nonNull(searcher, "searcher");
        this.onClose = Check.nonNull(onClose, "onClose");

        buildUI();
    }

    void focusField() {
        field.requestFocus();
        field.selectAll();
    }

    private void onKeyPressed(KeyEvent event) {
        switch (event.getCode()) {
            case ENTER -> {
                search(!event.isShiftDown(), false);
                event.consume();
            }
            case ESCAPE -> {
                onClose.run();
                event.consume();
            }
        }
    }

    private void search(boolean forward, boolean fromCurrent) {
        var found = searcher.find(field.getText(), forward, fromCurrent);
        field.getStyleClass().remove(ERROR_CLASS);
        if (!found) {
            field.getStyleClass().add(ERROR_CLASS);
        }
    }

    // region UI

    private void buildUI() {
        field.setPromptText("Find...");
        field.textProperty().addListener((_, _, _) -> search(true, true));
        field.addEventHandler(KeyEvent.KEY_PRESSED, this::onKeyPressed);
        HBox.setHgrow(field, Priority.ALWAYS);

        var previous = new Button("Previous");
        previous.setOnAction(_ -> search(false, false));

        var next = new Button("Next");
        next.setOnAction(_ -> search(true, false));

        var close = new Button("Close");
        close.setOnAction(_ -> onClose.run());

        setSpacing(4);
        setPadding(new Insets(4));
        setAlignment(Pos.CENTER_LEFT);
        getChildren().addAll(new Label("Find:"), field, previous, next, close);
    }

    // endregion

    @FunctionalInterface
    interface Searcher {
        boolean find(String query, boolean forward, boolean fromCurrent);
    }
}
