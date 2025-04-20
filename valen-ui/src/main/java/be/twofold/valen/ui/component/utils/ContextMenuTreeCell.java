package be.twofold.valen.ui.component.utils;

import be.twofold.valen.core.util.*;
import javafx.scene.control.*;

import java.util.*;
import java.util.function.*;

final class ContextMenuTreeCell<T> extends TreeCell<T> {
    private final ContextMenu contextMenu;
    private final BiConsumer<MenuItem, T> consumer;

    ContextMenuTreeCell(List<MenuItem> menuItems, BiConsumer<MenuItem, T> consumer) {
        this.contextMenu = new ContextMenu(menuItems.toArray(MenuItem[]::new));
        this.consumer = Check.notNull(consumer, "consumer");
        setContextMenu(contextMenu);

        for (var menuItem : this.contextMenu.getItems()) {
            menuItem.setOnAction(event -> {
                var item = (MenuItem) event.getSource();
                var value = getTreeItem().getValue();
                this.consumer.accept(item, value);
            });
        }
    }

    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);

        if (item == null || empty) {
            setText(null);
            setContextMenu(null);
        } else {
            setText(item.toString());
            setContextMenu(contextMenu);
        }
    }
}
