package org.redeye.dmf;


import java.util.*;

public class DMFMaterial {
    public final String name;
    public String type;
    public final Map<String, Integer> textureIds = new HashMap<>();
    public final List<DMFTextureDescriptor> textureDescriptors = new ArrayList<>();

    public DMFMaterial(String name) {
        this.name = name;
    }
}
