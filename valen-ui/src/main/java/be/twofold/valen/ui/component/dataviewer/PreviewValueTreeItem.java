package be.twofold.valen.ui.component.dataviewer;

import javafx.collections.*;
import javafx.scene.control.*;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.*;

final class PreviewValueTreeItem extends TreeItem<PreviewItem> {
    private boolean isFirstTimeChildren = true;

    public PreviewValueTreeItem(PreviewItem value) {
        super(value);
    }

    @Override
    public ObservableList<TreeItem<PreviewItem>> getChildren() {
        if (isFirstTimeChildren) {
            isFirstTimeChildren = false;
            super.getChildren().setAll(buildChildren(this.getValue().value()));
        }
        return super.getChildren();
    }

    @Override
    public boolean isLeaf() {
        var value = getValue().value();
        if (value == null) {
            return true;
        }
        if (value.getClass().isRecord()) {
            return false;
        }
        return switch (value) {
            case List<?> _, Map<?, ?> _ -> false;
            default -> true;
        };
    }

    private List<PreviewValueTreeItem> buildChildren(Object value) {
        if (value == null) {
            return List.of();
        }

        if (value.getClass().isRecord()) {
            return Arrays.stream(value.getClass().getRecordComponents())
                .map(c -> create(c.getName(), getComponent(value, c)))
                .toList();
        }

        return switch (value) {
            case Map<?, ?> map -> map.entrySet().stream()
                .map(e -> create(e.getKey().toString(), e.getValue()))
                .toList();
            case List<?> list -> IntStream.range(0, list.size())
                .mapToObj(i -> create(Integer.toString(i), list.get(i)))
                .toList();
            default -> List.of();
        };
    }

    private PreviewValueTreeItem create(String name, Object value) {
        return new PreviewValueTreeItem(new PreviewItem(name, value));
    }

    private Object getComponent(Object value, RecordComponent component) {
        try {
            return component.getAccessor().invoke(value);
        } catch (IllegalAccessException | InvocationTargetException e) {
            return e.getMessage();
        }
    }
}
