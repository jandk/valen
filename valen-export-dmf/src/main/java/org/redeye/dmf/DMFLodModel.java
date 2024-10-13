package org.redeye.dmf;


import java.util.*;

public class DMFLodModel extends DMFNode {
    public final List<Lod> lods = new ArrayList<>();

    public DMFLodModel(String name) {
        super(name, DMFNodeType.LOD);
    }

    public void addLod(DMFNode model, float distance) {
        lods.add(new Lod(model, lods.size(), distance));
    }

    public record Lod(DMFNode model, int id, float distance) {
    }
}
