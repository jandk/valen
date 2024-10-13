package org.redeye.dmf;


import java.io.*;
import java.util.*;

public class DMFSceneFile {
    public final DMFSceneMetaData metadata;
    public final List<DMFCollection> collections;
    public final List<DMFNode> models;
    public final List<DMFSkeleton> skeletons;
    public final List<DMFBufferView> bufferViews;
    public final List<DMFBuffer> buffers;
    public final List<DMFMaterial> materials;
    public final List<DMFTexture> textures;
    public final List<DMFNode> instances;

    public DMFSceneFile(int version) {

        Properties myProperties = new Properties();
        try {
            myProperties.load(getClass().getResourceAsStream("/version.properties"));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        String theVersion = Objects.requireNonNull((String) myProperties.get("dmf.version"));

        metadata = new DMFSceneMetaData("Valen (%s)".formatted(theVersion), version);
        collections = new ArrayList<>();
        models = new ArrayList<>();
        skeletons = new ArrayList<>();
        bufferViews = new ArrayList<>();
        buffers = new ArrayList<>();
        materials = new ArrayList<>();
        textures = new ArrayList<>();
        instances = new ArrayList<>();
    }

    public DMFMaterial getMaterial(String materialName) {
        for (DMFMaterial material : materials) {
            if (material.name.equals(materialName)) {
                return material;
            }
        }
        return null;
    }


    public DMFMaterial createMaterial(String materialName) {
        final DMFMaterial material = new DMFMaterial(materialName);
        materials.add(material);
        return material;
    }

    public DMFTexture getTexture(String textureName) {
        for (DMFTexture texture : textures) {
            if (texture.name.equals(textureName)) {
                return texture;
            }
        }
        return null;
    }


    public DMFCollection createCollection(String name) {
        return createCollection(name, null, true);
    }


    public DMFCollection createCollection(String name, DMFCollection parent, boolean enabled) {
        final DMFCollection collection = new DMFCollection(name, enabled, parent != null ? collections.indexOf(parent) : null);
        collections.add(collection);
        return collection;
    }

    public int getTextureId(DMFTexture texture) {
        if(!textures.contains(texture)) {
            textures.add(texture);
        }
        return textures.indexOf(texture);
    }

    public DMFBuffer createBuffer(String name, DMFBuffer.DataProvider provider) {
        var buffer = new DMFInternalBuffer(name, provider);
        buffers.add(buffer);
        return buffer;
    }

    public int getBufferId(DMFBuffer buffer) {
        if(!buffers.contains(buffer)) {
            buffers.add(buffer);
        }
        return buffers.indexOf(buffer);
    }
    public DMFBufferView createBufferView(int bufferId, int offset, int size) {
        var bufferView = new DMFBufferView(bufferId, offset, size);
        bufferViews.add(bufferView);
        return bufferView;
    }

    public int getBufferViewId(DMFBufferView bufferView) {
        if(!bufferViews.contains(bufferView)) {
            bufferViews.add(bufferView);
        }
        return bufferViews.indexOf(bufferView);
    }


}
