package be.twofold.valen;

import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.math.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.export.gltf.*;
import be.twofold.valen.export.gltf.model.ImmutableNodeSchema;
import be.twofold.valen.manager.*;
import be.twofold.valen.reader.compfile.CompFileReader;
import be.twofold.valen.reader.compfile.entities.EntityReader;
import be.twofold.valen.reader.staticinstances.*;
import be.twofold.valen.resource.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public final class MapExporter {
    static Map<String, Integer> meshCache = new HashMap<>();
    static Map<String, ImmutableNodeSchema.Builder> layersCache = new HashMap<>();

    public static void main(String[] args) throws IOException {
        var manager = new FileManager(Experiment.BASE);
        manager.select("game/dlc/hub/hub");

        Resource resource = manager.getEntries().stream()
                .filter(e -> e.type() == ResourceType.StaticInstances)
                .findFirst().orElseThrow();

        StaticInstances instances = StaticInstances.read(BetterBuffer.wrap(manager.readRawResource(resource)));

        Resource entityResource = manager.getEntries().stream()
                .filter(e -> e.name().name().endsWith(".entities")).findFirst().orElseThrow();
        EntityReader entityReader = new EntityReader(new CompFileReader());
        byte[] bytes = manager.readRawResource(entityResource);
        var entities = entityReader.read(BetterBuffer.wrap(bytes), entityResource);

        try (var channel = Files.newByteChannel(Path.of("map.glb"), StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
            GltfWriter writer = new GltfWriter(channel);
            writer.addUsedExtension("KHR_lights_punctual", true);
            JsonObject extensions = writer.getExtensions();
            JsonObject khrLightsPunctual = new JsonObject();
            JsonArray lights = new JsonArray();
            khrLightsPunctual.add("lights", lights);
            extensions.add("KHR_lights_punctual", khrLightsPunctual);
            var scene = writer.addScene();

            float sqrt22 = (float) (Math.sqrt(2) / 2);
            var rootNode = ImmutableNodeSchema.builder()
                    .name("Root")
                    .rotation(new Quaternion(-sqrt22, 0, 0, sqrt22))
                    .scale(new Vector3(0.7f, 0.7f, 0.7f))
                    .children(new ArrayList<>());

            for (int i = 0; i < instances.modelInstanceGeometries().size(); i++) {
                var geometry = instances.modelInstanceGeometries().get(i);
                var modelName = instances.models().get(geometry.modelIndex()).toLowerCase(Locale.ROOT);
                int meshId = meshCache.computeIfAbsent(modelName, k -> {
                    Model model = manager.readResource(FileType.StaticModel, modelName);
                    return writer.addMesh(model);
                });

                String name = instances.modelInstanceNames().get(i);
                Quaternion rotation = geometry.rotation().rotation();
                Vector3 translation = geometry.translation();
                Vector3 scale = geometry.scale();

                var parentLayer = layersCache.computeIfAbsent(instances.declLayers().get(geometry.declLayerIndex()), k -> ImmutableNodeSchema.builder().name(k));

                var node = ImmutableNodeSchema.builder()
                        .name(name)
                        .rotation(rotation)
                        .translation(translation)
                        .scale(scale).mesh(meshId)
                        .build();
                parentLayer.addChildren(writer.addNode(node));
//                scene.addNode(writer.addNode(node));
            }
            entities.entities().forEach((entityName, entity) -> {
                JsonObject entityData = entity.entityDef();
                if (!entityData.has("inherit")) {
                    var name = entityData.get("inherit").getAsString();
                    var inhObject = manager.getDeclManager().load("generated/decls/entitydef/" + name + ".decl");
                    manager.getDeclManager().merge(inhObject, entityData);
                }
                var entityEditData = entityData.get("edit").getAsJsonObject();
                if (entityEditData.has("spawnPosition") || entityEditData.has("spawnOrientation")) {
                    var sourceIOData = new JsonObject();
                    var sourceIOEntityData = new JsonObject();
                    JsonObject asJsonObject = entityEditData.getAsJsonObject();
                    asJsonObject.add("classname", entityData.get("class"));
                    sourceIOEntityData.add("entity", asJsonObject);
                    sourceIOData.add("entity_data", sourceIOEntityData);
                    var entityNodeBuilder = ImmutableNodeSchema.builder()
                            .name(entityName)
                            .extras(sourceIOData);
                    addRotation(entityNodeBuilder, entityEditData.get("spawnOrientation"));
                    addTranslation(entityNodeBuilder, entityEditData.get("spawnPosition"));
                    switch (entityData.get("class").getAsString()) {
                        case "idLight" -> {
                            String lightType = entityEditData.has("lightType") ? entityEditData.get("lightType").getAsString() : "NO_TYPE";
                            switch (lightType) {
                                case "LIGHT_PROBE" -> {
                                } // Do nothing
                                case "LIGHT_SPOT" -> {
                                    JsonObject light = new JsonObject();
                                    light.addProperty("name", entityName);
                                    Optional<Vector3> color = toColor(entityEditData.get("lightColor"));
                                    if (color.isPresent()) {
                                        JsonArray jColor = new JsonArray();
                                        jColor.add(color.get().x());
                                        jColor.add(color.get().y());
                                        jColor.add(color.get().z());
                                        light.add("color", jColor);
                                    }
                                    if (entityEditData.has("lightIntensity")) {
                                        light.addProperty("intensity", entityEditData.get("lightIntensity").getAsFloat() * 100);
                                    } else {
                                        light.addProperty("intensity", 1000.f);
                                    }
                                    light.addProperty("type", "spot");
                                    var spotLight = entityEditData.getAsJsonObject("spotLight");
                                    if (spotLight.has("lightConeSize") && spotLight.has("lightConeLength")) {
                                        light.addProperty("outerConeAngle",
                                                calculateSpotlightConeAngle(spotLight.get("lightConeSize").getAsFloat(),
                                                        spotLight.get("lightConeLength").getAsFloat()));
                                        light.addProperty("innerConeAngle",
                                                calculateSpotlightConeAngle(spotLight.get("lightConeSize").getAsFloat(),
                                                        spotLight.get("lightConeLength").getAsFloat()));
                                    }
                                    lights.add(light);
                                    int lightId = lights.size() - 1;
                                    JsonObject nodeData = new JsonObject();
                                    JsonObject nodeLightData = new JsonObject();
                                    nodeLightData.addProperty("light", lightId);
                                    nodeData.add("KHR_lights_punctual", nodeLightData);
                                    entityNodeBuilder.extensions(nodeData);

                                }
                                case "NO_TYPE" -> { // Default is point light??
                                    JsonObject light = new JsonObject();
                                    light.addProperty("name", entityName);
                                    Optional<Vector3> color = toColor(entityEditData.get("lightColor"));
                                    if (color.isPresent()) {
                                        JsonArray jColor = new JsonArray();
                                        jColor.add(color.get().x());
                                        jColor.add(color.get().y());
                                        jColor.add(color.get().z());
                                        light.add("color", jColor);
                                    }
                                    if (entityEditData.has("lightIntensity")) {
                                        light.addProperty("intensity", entityEditData.get("lightIntensity").getAsFloat() * 100);
                                    } else {
                                        light.addProperty("intensity", 1000.f);
                                    }
                                    light.addProperty("type", "point");
                                    lights.add(light);
                                    int lightId = lights.size() - 1;
                                    JsonObject nodeData = new JsonObject();
                                    JsonObject nodeLightData = new JsonObject();
                                    nodeLightData.addProperty("light", lightId);
                                    nodeData.add("KHR_lights_punctual", nodeLightData);
                                    entityNodeBuilder.extensions(nodeData);
                                }
                                default ->
                                        System.out.println("Unhandled light type: " + lightType + " " + entityEditData);
                            }

                            System.out.println(entity);
                        }
                        default -> System.out.println("Unhandled entity: " + entityData.get("class").getAsString());
                    }


                    rootNode.addChildren(writer.addNode(entityNodeBuilder.build()));
                }
            });
            layersCache.forEach((s, builder) -> {
                rootNode.addChildren(writer.addNode(builder.build()));
            });
            scene.addNode(writer.addNode(rootNode.build()));
            writer.write();
        }
    }

    private static void addRotation(ImmutableNodeSchema.Builder builder, JsonElement value) {
        if (value == null || value.isJsonNull()) {
            return;
        }
        var mat = value.getAsJsonObject().getAsJsonObject("mat");
        var col0 = toVec3(mat.getAsJsonObject("mat[0]")).orElse(new Vector3(1, 0, 0));
        var col1 = toVec3(mat.getAsJsonObject("mat[1]")).orElse(new Vector3(0, 1, 0));
        var col2 = toVec3(mat.getAsJsonObject("mat[2]")).orElse(new Vector3(0, 0, 1));
        builder.rotation(new Matrix3(
                col0.x(), col0.y(), col0.z(),
                col1.x(), col1.y(), col1.z(),
                col2.x(), col2.y(), col2.z()
        ).rotation());
    }

    private static void addTranslation(ImmutableNodeSchema.Builder builder, JsonElement value) {
        var vec3 = toVec3(value);
        if (vec3.isPresent()) {
            builder.translation(vec3);
        }
    }

    private static Optional<Vector3> toVec3(JsonElement value) {
        if (value == null || value.isJsonNull()) {
            return Optional.empty();
        }
        var vec = value.getAsJsonObject();
        return Optional.of(new Vector3(
                vec.has("x") ? vec.get("x").getAsNumber().floatValue() : 0,
                vec.has("y") ? vec.get("y").getAsNumber().floatValue() : 0,
                vec.has("z") ? vec.get("z").getAsNumber().floatValue() : 0
        ));
    }

    private static Optional<Vector3> toColor(JsonElement value) {
        if (value == null || value.isJsonNull()) {
            return Optional.empty();
        }
        var vec = value.getAsJsonObject();
        return Optional.of(new Vector3(
                vec.has("r") ? vec.get("r").getAsNumber().floatValue() : 1,
                vec.has("g") ? vec.get("g").getAsNumber().floatValue() : 1,
                vec.has("b") ? vec.get("b").getAsNumber().floatValue() : 1
        ));
    }

    public static float calculateSpotlightConeAngle(float width, float height) {
        // Check for invalid input
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Width and height must be positive.");
        }

        // Calculate radius and angle
        float radius = width / 2;
        float angle = (float) Math.atan(radius / height);

        // Return angle in degrees by default
        return angle;
    }

}