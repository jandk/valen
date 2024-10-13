package org.redeye.dmf;


import java.util.*;

public class DMFNode {
    public final DMFNodeType type;
    public final String name;

    public final List<DMFNode> children = new ArrayList<>();
    public List<Integer> collectionIds = new ArrayList<>();
    public DMFTransform transform = null;

    public DMFNode(String name) {
        this(name, DMFNodeType.NODE);
    }

    protected DMFNode(String name, DMFNodeType type) {
        this.name = name;
        this.type = type;
    }

    public void addToCollection(DMFCollection collection, DMFSceneFile scene) {
        collectionIds.add(scene.collections.indexOf(collection));
    }
}
