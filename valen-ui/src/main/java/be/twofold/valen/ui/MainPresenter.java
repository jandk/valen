package be.twofold.valen.ui;

import be.twofold.valen.resource.*;
import jakarta.inject.*;

import javax.swing.tree.*;
import java.util.*;

public class MainPresenter {
    private final MainView view;
    private Collection<Resource> entries;

    @Inject
    MainPresenter(MainView view) {
        this.view = view;
        this.view.addListener(this::loadResources);
    }

    public void show() {
        view.show();
    }

    public void setResources(Collection<Resource> entries) {
        this.entries = entries;
        view.setFileTree(convert(buildNodeTree(entries)));
    }

    private void loadResources(String path) {
        if (entries == null) {
            return;
        }
        var resources = entries.stream()
            .filter(r -> r.name().path().equals(path))
            .toList();

        view.setResources(resources);
    }

    private MutableTreeNode convert(Node node) {
        List<Node> children = node.children.entrySet().stream()
            .sorted(Map.Entry.comparingByKey(NaturalOrderComparator.instance()))
            .map(Map.Entry::getValue)
            .toList();

        var item = new DefaultMutableTreeNode(node.name);
        for (Node child : children) {
            item.add(convert(child));
        }
        return item;
    }

    private Node buildNodeTree(Collection<Resource> entries) {
        Node root = new Node("root");
        for (Resource entry : entries) {
            Node node = root;
            String path = entry.name().path();
            if (!path.isEmpty()) {
                for (String s : path.split("/")) {
                    node = node.get(s);
                }
            }
        }
        return root;
    }

    private static final class Node {
        private final String name;
        private final Map<String, Node> children = new HashMap<>();

        public Node(String name) {
            this.name = name;
        }

        public Node get(String name) {
            return children.computeIfAbsent(name, Node::new);
        }
    }
}
