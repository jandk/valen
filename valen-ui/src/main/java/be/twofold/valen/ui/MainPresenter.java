package be.twofold.valen.ui;

import be.twofold.valen.resource.*;

import javax.inject.*;
import javax.swing.tree.*;
import java.util.*;

public class MainPresenter {
    private final MainView view;

    @Inject
    public MainPresenter(MainView view) {
        this.view = view;
    }

    public void show() {
        view.show();
    }

    public void setResources(Collection<Resource> entries) {
        view.setFileTree(convert(buildNodeTree(entries)));
    }

    private MutableTreeNode convert(Node node) {
        List<Node> children = node.children.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
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
