package be.twofold.valen.ui.component.metaview;

import be.twofold.valen.core.util.*;
import javafx.collections.*;
import javafx.scene.control.*;

import java.util.*;
import java.util.stream.*;

final class MetaTreeItem extends TreeItem<Meta.Item> {
    private boolean firstTime = true;

    MetaTreeItem(Meta.Item value) {
        super(value);
    }

    @Override
    public ObservableList<TreeItem<Meta.Item>> getChildren() {
        if (firstTime) {
            firstTime = false;
            super.getChildren().setAll(buildChildren());
        }
        return super.getChildren();
    }

    @Override
    public boolean isLeaf() {
        return switch (getValue().node()) {
            case Meta.Sequence sequence -> sequence.items().isEmpty();
            case Meta.Struct struct -> struct.fields().isEmpty();
            case Meta.Value _ -> true;
        };
    }

    private List<MetaTreeItem> buildChildren() {
        return switch (getValue().node()) {
            case Meta.Sequence sequence -> IntStream.range(0, sequence.items().size())
                .mapToObj(i -> new MetaTreeItem(new Meta.Item("[" + i + "]", sequence.items().get(i))))
                .toList();
            case Meta.Struct struct -> struct.fields().stream()
                .map(e -> new MetaTreeItem(new Meta.Item(e.name(), e.node())))
                .toList();
            case Meta.Value _ -> List.of();
        };
    }
}
