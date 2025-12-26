package be.twofold.valen.ui.component.utils;

import be.twofold.valen.core.util.*;
import javafx.scene.control.*;
import javafx.util.*;
import javafx.util.converter.*;

public final class TooltippedTableCell<S, T> extends TableCell<S, T> {
    private final StringConverter<T> converter;
    private final Tooltip tooltip = new Tooltip();

    private TooltippedTableCell(StringConverter<T> converter) {
        this.converter = Check.nonNull(converter, "converter");

        textTruncatedProperty().addListener((_, _, newValue) -> {
            setTooltip(newValue ? tooltip : null);
        });
    }

    public static <S> Callback<TableColumn<S, String>, TableCell<S, String>> forTableColumn() {
        return forTableColumn(new DefaultStringConverter());
    }

    public static <S, T> Callback<TableColumn<S, T>, TableCell<S, T>> forTableColumn(StringConverter<T> converter) {
        return _ -> new TooltippedTableCell<>(converter);
    }

    @Override
    public void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);

        if (isEmpty()) {
            setText(null);
            setTooltip(null);
            return;
        }

        var text = converter.toString(item);
        tooltip.setText(text);

        setText(text);
        setTooltip(isTextTruncated() ? tooltip : null);
    }
}
