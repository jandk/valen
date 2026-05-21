package be.twofold.valen.ui.component.metaview;

import be.twofold.valen.core.util.*;
import be.twofold.valen.ui.common.*;
import jakarta.inject.*;
import javafx.beans.property.*;
import javafx.scene.*;
import javafx.scene.control.*;

import java.lang.reflect.*;

@Singleton
public final class MetaViewImpl extends AbstractView<View.Listener> implements MetaView {
    private final TreeTableView<Meta.Item> treeTableView = new TreeTableView<>();

    @Inject
    MetaViewImpl() {
        var nameColumn = new TreeTableColumn<Meta.Item, String>("Name");
        nameColumn.setCellValueFactory(param -> {
            var item = param.getValue().getValue();
            return new SimpleStringProperty(item.name());
        });
        nameColumn.setPrefWidth(200);

        var typeColumn = new TreeTableColumn<Meta.Item, String>("Type");
        typeColumn.setCellValueFactory(param -> {
            var item = param.getValue().getValue();
            var type = switch (item.node()) {
                case Meta.Sequence sequence -> "[" + sequence.items().size() + " items]";
                case Meta.Struct struct -> struct.typeName();
                case Meta.Value value -> value.raw() == null ? "void" : value.raw().getClass().getSimpleName();
            };
            return new SimpleStringProperty(type);
        });
        typeColumn.setPrefWidth(150);

        var valueColumn = new TreeTableColumn<Meta.Item, String>("Value");
        valueColumn.setCellValueFactory(param -> {
            var item = param.getValue().getValue();
            var value = item.node() instanceof Meta.Value v ? format(v.raw()) : "";
            return new SimpleStringProperty(value);
        });
        valueColumn.setPrefWidth(250);

        treeTableView.getColumns().addAll(nameColumn, typeColumn, valueColumn);
        treeTableView.setShowRoot(true);
        treeTableView.setColumnResizePolicy(TreeTableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
    }

    @Override
    public Parent getFXNode() {
        return treeTableView;
    }

    @Override
    public void setRoot(Meta.Node root) {
        var name = switch (root) {
            case Meta.Sequence sequence -> "[" + sequence.items().size() + "]";
            case Meta.Struct struct -> struct.typeName();
            case Meta.Value value -> String.valueOf(value.raw());
        };
        var rootItem = new MetaTreeItem(new Meta.Item(name, root));
        rootItem.setExpanded(true);
        treeTableView.setRoot(rootItem);
    }

    @Override
    public void clear() {
        treeTableView.setRoot(null);
    }

    private String format(Object object) {
        if (object == null) {
            return "null";
        }
        if (object.getClass().isArray()) {
            return object.getClass().componentType().getSimpleName() + "[" + Array.getLength(object) + "]";
        }
        return object.toString();
    }
}
