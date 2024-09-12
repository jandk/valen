package be.twofold.valen.ui.viewer.data;

import javafx.collections.*;
import javafx.scene.control.*;

import java.lang.reflect.*;
import java.util.*;

final class PreviewValueTreeItem extends TreeItem<PreviewItem> {
    private boolean isLeaf;
    private boolean isFirstTimeChildren = true;
    private boolean isFirstTimeLeaf = true;

    public PreviewValueTreeItem(PreviewItem value) {
        super(value);
    }

    @Override
    public ObservableList<TreeItem<PreviewItem>> getChildren() {
        if (isFirstTimeChildren) {
            isFirstTimeChildren = false;
            try {
                super.getChildren().setAll(buildChildren(this));
            } catch (InvocationTargetException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return super.getChildren();
    }

    @Override
    public boolean isLeaf() {
        if (isFirstTimeLeaf) {
            isFirstTimeLeaf = false;
            var holder = getValue();
            if (holder.getClass().isRecord()) {
                isLeaf = false;
            } else {
                switch (holder.value()) {
                    case Map<?, ?> ignored -> isLeaf = false;
                    case List<?> ignored -> isLeaf = false;
                    case null, default -> isLeaf = true;
                }
            }
        }
        return isLeaf;
    }

    private ObservableList<PreviewValueTreeItem> buildChildren(PreviewValueTreeItem item) throws InvocationTargetException, IllegalAccessException {
        var holder = item.getValue();
        if (holder.value() != null) {
            ObservableList<PreviewValueTreeItem> children = FXCollections.observableArrayList();
            if (holder.value().getClass().isRecord()) {
                var value = holder.value();
                for (RecordComponent recordComponent : value.getClass().getRecordComponents()) {
                    children.add(new PreviewValueTreeItem(new PreviewItem(recordComponent.getName(), recordComponent.getAccessor().invoke(value))));
                }
            } else {
                switch (holder.value()) {
                    case Map<?, ?> map -> {
                        map.forEach((key, value) -> {
                            children.add(new PreviewValueTreeItem(new PreviewItem(key.toString(), value)));
                        });
                    }
                    case List<?> list -> {
                        for (int i = 0; i < list.size(); i++) {
                            children.add(new PreviewValueTreeItem(new PreviewItem(Integer.toString(i), list.get(i))));
                        }
                    }
                    default -> {
                        return FXCollections.emptyObservableList();
                    }
                }
            }
            return children;
        }
        return FXCollections.emptyObservableList();
    }
}
