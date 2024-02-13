package be.twofold.valen;

import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.math.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.export.gltf.*;
import be.twofold.valen.export.gltf.model.ImmutableNodeSchema;
import be.twofold.valen.export.gltf.model.extensions.lightspunctual.*;
import be.twofold.valen.manager.*;
import be.twofold.valen.reader.compfile.CompFileReader;
import be.twofold.valen.reader.compfile.entities.EntityReader;
import be.twofold.valen.reader.staticinstances.*;
import be.twofold.valen.resource.*;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public final class MapExporter {
    private static final Set<String> BlackList = Set.of(
            "editors/models/gui_text.lwo",
            "models/ca/working/bshore/darrow1.lwo",
            "models/guis/gui_square.lwo",
            "models/guis/gui_square_afterpost.lwo"
    );
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
        var entities = entityReader.read(BetterBuffer.wrap(manager.readRawResource(entityResource)), null);
        try (var channel = Files.newByteChannel(Path.of("map.glb"), StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
            GltfWriter writer = new GltfWriter(channel);
            KHRLightsPunctualExtension khrLightsPunctual = new KHRLightsPunctualExtension();
            writer.addExtension("KHR_lights_punctual", khrLightsPunctual);
            writer.addUsedExtension("KHR_lights_punctual", true);
            var scene = writer.addScene();

            var rootNode = ImmutableNodeSchema.builder()
                    .name("Root")
                    .rotation(new Quaternion(-(float) (Math.sqrt(2) / 2), 0, 0, (float) (Math.sqrt(2) / 2)))
                    .scale(new Vector3(0.7f, 0.7f, 0.7f))
                    .children(new ArrayList<>());

            for (int i = 0; i < instances.modelInstanceGeometries().size(); i++) {
                var geometry = instances.modelInstanceGeometries().get(i);
                var modelName = instances.models().get(geometry.modelIndex()).toLowerCase(Locale.ROOT);
                int meshId = meshCache.computeIfAbsent(modelName, k -> {
                    Model model = manager.readResource(FileType.StaticModel, modelName, ResourceType.Model);
                    return writer.addMesh(model);
                });

                String name = instances.modelInstanceNames().get(i);
                Quaternion rotation = geometry.rotation().rotation();
                Vector3 translation = geometry.translation();
                Vector3 scale = geometry.scale();

                var parentLayer = layersCache.computeIfAbsent(instances.declLayers().get(geometry.declLayerIndex()), k -> {
                    var builder = ImmutableNodeSchema.builder();
                    builder.name(k);
                    JsonObject extraData = new JsonObject();
                    extraData.addProperty("type", "collection");
                    builder.extras(extraData);
                    return builder;
                });

                var node = ImmutableNodeSchema.builder()
                        .name(name)
                        .rotation(rotation)
                        .translation(translation)
                        .scale(scale).mesh(meshId)
                        .build();
                parentLayer.addChildren(writer.addNode(node));
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
                    String entityClass = entityData.get("class").getAsString();
                    switch (entityClass) {
                        case "idLight" -> {
                            String lightType = entityEditData.has("lightType") ? entityEditData.get("lightType").getAsString() : "NO_TYPE";
                            switch (lightType) {
                                case "LIGHT_PROBE" -> {
                                } // Do nothing
                                case "LIGHT_SCATTERING" -> {
                                } // Do nothing
                                case "LIGHT_SPOT" -> {
                                    ImmutableSpotLightSchema.Builder lightBuilder = ImmutableSpotLightSchema.builder();
                                    lightBuilder.name(entityName);
                                    Optional<Vector3> color = toColor(entityEditData.get("lightColor"));
                                    color.ifPresent(lightBuilder::color);
                                    if (entityEditData.has("lightIntensity")) {
                                        lightBuilder.intensity(entityEditData.get("lightIntensity").getAsFloat() * 100);
                                    } else {
                                        lightBuilder.intensity(1000.f);
                                    }
                                    lightBuilder.type(LightType.spot);
                                    var spotLight = entityEditData.getAsJsonObject("spotLight");
                                    if (spotLight.has("lightConeSize") && spotLight.has("lightConeLength")) {
                                        lightBuilder.outerConeAngle(calculateSpotlightConeAngle(spotLight.get("lightConeSize").getAsFloat(),
                                                spotLight.get("lightConeLength").getAsFloat()));
                                        lightBuilder.innerConeAngle(calculateSpotlightConeAngle(spotLight.get("lightConeSize").getAsFloat(),
                                                spotLight.get("lightConeLength").getAsFloat()));
                                    }
                                    khrLightsPunctual.lights.add(lightBuilder.build());
                                    int lightId = khrLightsPunctual.lights.size() - 1;
                                    ImmutableKHRLightsPunctualNodeExtension.Builder nodeExtension = ImmutableKHRLightsPunctualNodeExtension.builder();
                                    nodeExtension.light(lightId);
                                    entityNodeBuilder.putExtensions("KHR_lights_punctual", nodeExtension.build());

                                }
                                case "NO_TYPE" -> { // Default is point light??
                                    ImmutablePointLightSchema.Builder lightBuilder = ImmutablePointLightSchema.builder();
                                    lightBuilder.name(entityName);
                                    Optional<Vector3> color = toColor(entityEditData.get("lightColor"));
                                    color.ifPresent(lightBuilder::color);
                                    lightBuilder.type(LightType.point);
                                    if (entityEditData.has("lightIntensity")) {
                                        lightBuilder.intensity(entityEditData.get("lightIntensity").getAsFloat() * 100);
                                    } else {
                                        lightBuilder.intensity(1000.f);
                                    }
                                    khrLightsPunctual.lights.add(lightBuilder.build());
                                    int lightId = khrLightsPunctual.lights.size() - 1;
                                    ImmutableKHRLightsPunctualNodeExtension.Builder nodeExtension = ImmutableKHRLightsPunctualNodeExtension.builder();
                                    nodeExtension.light(lightId);
                                    entityNodeBuilder.putExtensions("KHR_lights_punctual", nodeExtension.build());
                                }
                                default ->
                                        System.out.println("Unhandled light type: " + lightType + " " + entityEditData);
                            }
                        }
                        case "idGuiEntity", "idDynamicEntity" -> {
                            JsonObject modelInfo = entityEditData.getAsJsonObject("renderModelInfo");
                            String modelName = modelInfo.get("model").getAsString();
                            int meshId = meshCache.computeIfAbsent(modelName, k -> {
                                try {
                                    if (BlackList.contains(modelName)) {
                                        return -1;
                                    }
                                    Model model = manager.readResource(FileType.StaticModel, modelName, ResourceType.Model);
                                    return writer.addMesh(model);
                                } catch (IllegalArgumentException ex) {
                                    System.err.println("Failed to find " + modelName + " model");
                                    return -1;
                                }
                            });
                            if (meshId != -1) {
                                entityNodeBuilder.mesh(meshId);
                            }
                            if (modelInfo.has("scale")) {
                                toVec3(modelInfo.get("scale")).ifPresent(entityNodeBuilder::scale);
                            }
                        }
//                        case "idAnimated" -> {
//                            JsonObject modelInfo = entityEditData.getAsJsonObject("renderModelInfo");
//                            String modelName = modelInfo.get("model").getAsString();
//                            int meshId = meshCache.computeIfAbsent(modelName, k -> {
//                                System.out.println("Loading idDynamicEntity with model " + modelName);
//                                try {
//                                    Model model = manager.readResource(FileType.AnimatedModel, modelName, ResourceType.BinaryMd6def);
//                                    return writer.addMesh(model);
//                                } catch (IllegalArgumentException ex) {
//                                    System.err.println("Failed to find "+modelName+" model");
//                                    return -1;
//                                }
//                            });
//                            if (meshId != -1) {
//                                entityNodeBuilder.mesh(meshId);
//                            }
//                            if (modelInfo.has("scale")) {
//                                toVec3(modelInfo.get("scale")).ifPresent(entityNodeBuilder::scale);
//                            }
//                        }
                        default -> System.out.println("Unhandled entity: " + entityClass);
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
        return (float) Math.atan(width / 2 / height);
    }

}