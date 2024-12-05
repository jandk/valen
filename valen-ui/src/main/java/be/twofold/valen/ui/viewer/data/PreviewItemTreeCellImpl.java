package be.twofold.valen.ui.viewer.data;

import javafx.scene.control.*;

import java.lang.reflect.*;

final class PreviewItemTreeCellImpl extends TreeCell<PreviewItem> {
    @Override
    public void updateItem(PreviewItem item, boolean empty) {
        super.updateItem(item, empty);

        if (empty) {
            setText(null);
            setGraphic(null);
        } else {
            setText(getString());
            setGraphic(getTreeItem().getGraphic());
        }
    }

    private String getString() {
        var item = getItem();
        if (item == null) {
            return "null";
        }

        return item.name() + ": " + toString(item.value());
    }

    private String toString(Object value) {
        if (value == null) {
            return "null";
        }
        if (value.getClass().isArray()) {
            return "%s[%d]".formatted(value.getClass().componentType().getSimpleName(), Array.getLength(value));
        }
        return value.toString();
    }
}
