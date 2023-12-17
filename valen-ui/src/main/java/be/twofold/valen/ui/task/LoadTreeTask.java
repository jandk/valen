package be.twofold.valen.ui.task;

import be.twofold.valen.resource.*;
import javafx.concurrent.*;
import javafx.scene.control.*;

import java.util.*;

public class LoadTreeTask extends Task<TreeItem<String>> {
    private final Collection<Resource> entries;

    public LoadTreeTask(Collection<Resource> entries) {
        this.entries = entries;
    }

    @Override
    protected TreeItem<String> call() {
        return convert(buildNodeTree());
    }

    private TreeItem<String> convert(Node node) {
        List<Node> children = node.children.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .map(Map.Entry::getValue)
            .toList();

        TreeItem<String> item = new TreeItem<>(node.name);
        for (Node child : children) {
            item.getChildren().add(convert(child));
        }
        return item;
    }

    private Node buildNodeTree() {
        Node root = new Node("root");
        for (Resource entry : entries) {
            Node node = root;
            String path = entry.name().path();
            if (!path.isEmpty()) {
                for (String s : path.split("/")) {
                    if (s.isEmpty()) {
                        System.out.println(s);
                    }
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
