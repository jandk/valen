package be.twofold.valen.ui.viewers.data;

import javafx.scene.control.*;

import java.lang.reflect.*;

import static be.twofold.valen.ui.viewers.data.PreviewItem.*;

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

        Object value = item.value();
        String strValue;

        if (value == NOVALUE) {
            return item.name();
        } else if (value == null) {
            strValue = "null";
        } else if (item.value().getClass().isArray()) {
            strValue = "%s[%d]".formatted(item.value().getClass().componentType().getSimpleName(), Array.getLength(item.value()));
        } else {
            strValue = item.value().toString();
        }

        return "%s: %s".formatted(item.name(), strValue);
    }
}
