package be.twofold.valen;

import be.twofold.valen.core.math.Matrix3;
import be.twofold.valen.core.math.Quaternion;
import be.twofold.valen.core.math.Vector3;
import be.twofold.valen.core.util.BetterBuffer;
import be.twofold.valen.export.gltf.GltfWriter;
import be.twofold.valen.export.gltf.model.*;
import be.twofold.valen.export.gltf.model.extensions.collections.*;
import be.twofold.valen.export.gltf.model.extensions.lightspunctual.*;
import be.twofold.valen.manager.FileManager;
import be.twofold.valen.manager.FileType;
import be.twofold.valen.reader.compfile.CompFileReader;
import be.twofold.valen.reader.compfile.entities.EntityReader;
import be.twofold.valen.reader.decl.md6def.*;
import be.twofold.valen.reader.staticinstances.StaticInstances;
import be.twofold.valen.resource.ResourceType;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;

public final class MapExporter {
    private static final Set<String> BlackList = Set.of(
        "editors/models/gui_text.lwo",
        "models/ca/working/bshore/darrow1.lwo",
        "models/guis/gui_square.lwo",
        "models/guis/gui_square_afterpost.lwo"
    );
    static Map<String, MeshId> meshCache = new LinkedHashMap<>();
    static Map<String, NodeSchema.Builder> layersCache = new LinkedHashMap<>();
    static Map<String, NodeSchema.Builder> groupsCache = new LinkedHashMap<>();
    static Map<String, CollectionTreeNodeSchema.Builder> collectionCache = new LinkedHashMap<>();

    public static void main(String[] args) throws IOException {
        var manager = new FileManager(Experiment.BASE);
//        manager.select("game/hub/hub");
        manager.select("game/dlc/hub/hub");

        var resource = manager.getEntries().stream()
            .filter(e -> e.type() == ResourceType.StaticInstances)
            .findFirst().orElseThrow();

        var instances = StaticInstances.read(BetterBuffer.wrap(manager.readRawResource(resource)));

        var entityResource = manager.getEntries().stream()
            .filter(e -> e.name().name().endsWith(".entities")).findFirst().orElseThrow();
        var entityReader = new EntityReader(new CompFileReader());
        var entities = entityReader.read(BetterBuffer.wrap(manager.readRawResource(entityResource)), null);
        var md6DefParser = new MD6DefParser();

        try (var channel = Files.newByteChannel(Path.of("map.glb"), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)) {
            var writer = new GltfWriter(channel);
            List<LightSchema> lights = new ArrayList<>();
            var sceneNodes = new ArrayList<NodeId>();

            var rootNode = NodeSchema.builder()
                .name("Root")
                .rotation(new Quaternion(-(float) (Math.sqrt(2) / 2), 0, 0, (float) (Math.sqrt(2) / 2)))
                .scale(new Vector3(0.7f, 0.7f, 0.7f))
                .children(new ArrayList<>());

            var staticWorldNode = NodeSchema.builder().name("StaticWorld");
            {
                var extraData = new JsonObject();
                extraData.addProperty("type", "collection");
                staticWorldNode.extras(extraData);
            }
            var rootCollection = CollectionTreeNodeSchema.builder().collection("Root");
            var staticWorldCollection = CollectionTreeNodeSchema.builder().collection("StaticWorld").parent("Root");
            var entitiesCollection = CollectionTreeNodeSchema.builder().collection("Entities").parent("Root");
            collectionCache.put("Root", rootCollection);
            collectionCache.put("StaticWorld", staticWorldCollection);
            collectionCache.put("Entities", entitiesCollection);
            groupsCache.put("StaticWorld", staticWorldNode);

            for (var i = 0; i < instances.modelInstanceGeometries().size(); i++) {
                var geometry = instances.modelInstanceGeometries().get(i);
                var modelName = instances.models().get(geometry.modelIndex()).toLowerCase(Locale.ROOT);
                var meshId = meshCache.computeIfAbsent(modelName, k -> {
                    try {
                        var model = manager.readResource(FileType.StaticModel, modelName);
                        return writer.addMesh(model);

                    } catch (IllegalArgumentException ex) {
                        System.err.println("Failed to find " + modelName + " model");
                        return null;
                    }
                });
                if (meshId == null) {
                    continue;
                }

                var name = instances.modelInstanceNames().get(i);
                var rotation = geometry.rotation().rotation();
                var translation = geometry.translation();
                var scale = geometry.scale();

                var layerName = instances.declLayers().get(geometry.declLayerIndex());
                var parentLayer = layersCache.computeIfAbsent(layerName, k -> {
                    var builder = NodeSchema.builder();
                    builder.name(k);
                    var extraData = new JsonObject();
                    extraData.addProperty("type", "collection");
                    builder.extras(extraData);
                    return builder;
                });

                collectionCache.computeIfAbsent(layerName, k -> CollectionTreeNodeSchema.builder().collection(k).parent("StaticWorld"));

                var node = NodeSchema.builder()
                    .name(name)
                    .rotation(rotation)
                    .translation(translation)
                    .scale(scale).mesh(meshId)
                    .putExtensions("EXT_collections", EXTCollectionNodeExtensionSchema.builder().addCollections(layerName).build())
                    .build();
                parentLayer.addChildren(writer.addNode(node));
            }

            entities.entities().forEach((entityName, entity) -> {
                var entityData = entity.entityDef();
                if (!entityData.has("inherit")) {
                    var name = entityData.get("inherit").getAsString();
                    var inhObject = manager.getDeclManager().load("generated/decls/entitydef/" + name + ".decl");
                    manager.getDeclManager().merge(inhObject, entityData);
                }
                var entityEditData = entityData.get("edit").getAsJsonObject();
//                if (entityEditData.has("spawnPosition") || entityEditData.has("spawnOrientation")) {
                var sourceIOData = new JsonObject();
                var sourceIOEntityData = new JsonObject();
                var asJsonObject = entityEditData.getAsJsonObject();
                asJsonObject.add("classname", entityData.get("class"));
                sourceIOEntityData.add("entity", asJsonObject);
                sourceIOData.add("entity_data", sourceIOEntityData);
                var entityNodeBuilder = NodeSchema.builder()
                    .name(entityName)
                    .extras(sourceIOData);
                var rotation = new Quaternion(0, 0, 0, 1);
                if (entityEditData.has("spawnPosition") || entityEditData.has("spawnOrientation")) {
                    rotation = toQuaterion(entityEditData.get("spawnOrientation")).orElse(rotation);
                    addTranslation(entityNodeBuilder, entityEditData.get("spawnPosition"));
                } else {
                    entityNodeBuilder.translation(new Vector3(0, 0, 0));
                }
                var entityClass = entityData.get("class").getAsString();
                switch (entityClass) {
                    case "idLight" -> {
                        rotation = rotation.multiply(new Quaternion(0, -(float) (Math.sqrt(2) / 2), 0, (float) (Math.sqrt(2) / 2)));
                        var lightType = entityEditData.has("lightType") ? entityEditData.get("lightType").getAsString() : "NO_TYPE";
                        switch (lightType) {
                            case "LIGHT_PROBE", "LIGHT_SCATTERING" -> {
                                // Do nothing
                            }
                            case "LIGHT_SPOT" -> {
                                var lightBuilder = SpotLightSchema.builder();
                                lightBuilder.name(entityName);
                                var color = toColor(entityEditData.get("lightColor"));
                                color.ifPresent(lightBuilder::color);
                                if (entityEditData.has("lightIntensity")) {
                                    lightBuilder.intensity(entityEditData.get("lightIntensity").getAsFloat() * 100);
                                } else {
                                    lightBuilder.intensity(1000.f);
                                }
                                lightBuilder.type(LightType.spot);
                                var spotLight = entityEditData.getAsJsonObject("spotLight");
                                if (spotLight != null && spotLight.has("lightConeSize") && spotLight.has("lightConeLength")) {
                                    var spot = SpotSchema.builder()
                                        .outerConeAngle(calculateSpotlightConeAngle(spotLight.get("lightConeSize").getAsFloat(), spotLight.get("lightConeLength").getAsFloat()))
                                        .innerConeAngle(calculateSpotlightConeAngle(spotLight.get("lightConeSize").getAsFloat(), spotLight.get("lightConeLength").getAsFloat()) * 0.9f)
                                        .build();
                                    lightBuilder.spot(spot);
                                } else {
                                    lightBuilder.spot(SpotSchema.builder().build());
                                }
                                lights.add(lightBuilder.build());
                                var nodeExtension = KHRLightsPunctualNodeExtensionSchema.builder();
                                nodeExtension.light(LightId.of(lights.size() - 1));
                                entityNodeBuilder.putExtensions("KHR_lights_punctual", nodeExtension.build());

                            }
                            case "NO_TYPE", "LIGHT_POINT" -> { // Default is point light??
                                var lightBuilder = PointLightSchema.builder();
                                lightBuilder.name(entityName);
                                var color = toColor(entityEditData.get("lightColor"));
                                color.ifPresent(lightBuilder::color);
                                lightBuilder.type(LightType.point);
                                if (entityEditData.has("lightIntensity")) {
                                    lightBuilder.intensity(entityEditData.get("lightIntensity").getAsFloat() * 100);
                                } else {
                                    lightBuilder.intensity(1000.f);
                                }
                                lights.add(lightBuilder.build());
                                var nodeExtension = KHRLightsPunctualNodeExtensionSchema.builder();
                                nodeExtension.light(LightId.of(lights.size() - 1));
                                entityNodeBuilder.putExtensions("KHR_lights_punctual", nodeExtension.build());
                            }
                            default -> System.out.println("Unhandled light type: " + lightType + " " + entityEditData);
                        }
                    }
                    case "idGuiEntity", "idDynamicEntity", "idMover" -> {
                        var modelInfo = entityEditData.getAsJsonObject("renderModelInfo");
                        var modelName = modelInfo.get("model").getAsString();
                        var meshId = meshCache.computeIfAbsent(modelName, k -> {
                            try {
                                var tmp = modelName;
                                if (BlackList.contains(tmp)) {
                                    return null;
                                }
                                if (!tmp.endsWith(".lwo")) {
                                    tmp += ".bmodel";
                                }

                                var model = manager.readResource(FileType.StaticModel, tmp);
                                return writer.addMesh(model);
                            } catch (IllegalArgumentException ex) {
                                System.err.println("Failed to find " + modelName + " model");
                                return null;
                            }
                        });
                        if (meshId != null) {
                            entityNodeBuilder.mesh(meshId);
                        }
                        if (modelInfo.has("scale")) {
                            toVec3(modelInfo.get("scale")).ifPresent(entityNodeBuilder::scale);
                        }
                    }
//                    case "idTrigger" -> {
//                        var modelInfo = entityEditData.getAsJsonObject("clipModelInfo");
//                        var modelName = modelInfo.get("clipModelName").getAsString();
//                        var meshId = meshCache.computeIfAbsent(modelName, k -> {
//                            try {
//                                var tmp = modelName+".hkshape";
//                                if (BlackList.contains(tmp)) {
//                                    return null;
//                                }
//
//                                var model = manager.readResource(FileType.StaticModel, tmp, ResourceType.HavokShape);
//                                return writer.addMesh(model);
//                            } catch (IllegalArgumentException ex) {
//                                System.err.println("Failed to find " + modelName + " model");
//                                return null;
//                            }
//                        });
//                        if (meshId != null) {
//                            entityNodeBuilder.mesh(meshId);
//                        }
//                        if (modelInfo.has("scale")) {
//                            toVec3(modelInfo.get("scale")).ifPresent(entityNodeBuilder::scale);
//                        }
//                    }
//                    case "idAnimated" -> {
//                        JsonObject modelInfo = entityEditData.getAsJsonObject("renderModelInfo");
//                        String modelName = modelInfo.get("model").getAsString();
//                        System.out.println("Loading idDynamicEntity with model " + modelName);
//                        String md6declName = "generated/decls/md6def/" + modelName + ".decl";
//
//                        if (manager.exist(md6declName)) {
//                            String src = new String(manager.readRawResource(md6declName, ResourceType.RsStreamFile));
//                            JsonObject md6def = md6DefParser.parse(src);
//                            String meshPath = md6def.getAsJsonObject("init").getAsJsonPrimitive("mesh").getAsString();
//                            var model = manager.readResource(FileType.AnimatedModel, meshPath, ResourceType.BaseModel);
//                            MeshId meshId = writer.addMesh(model);
//                            entityNodeBuilder.mesh(meshId);
//                            if(model.skeleton()!=null) {
//                                var skeletonAndSkin = writer.addSkin(model.skeleton());
//                                sceneNodes.add(skeletonAndSkin.getKey());
//                                entityNodeBuilder.skin(skeletonAndSkin.getValue());
//                            }
//                        } else {
//                            System.err.println("Failed to resource for " + modelName);
//                        }
//
//                        if (modelInfo.has("scale")) {
//                            toVec3(modelInfo.get("scale")).ifPresent(entityNodeBuilder::scale);
//                        }
//                    }
                    default -> System.out.println("Unhandled entity: " + entityClass);
                }
                entityNodeBuilder.rotation(rotation);
                var parentLayer = groupsCache.computeIfAbsent(entityClass, k -> {
                    var extraData = new JsonObject();
                    extraData.addProperty("type", "collection");
                    return NodeSchema.builder().name(k).extras(extraData);
                });
                collectionCache.computeIfAbsent(entityClass, k -> CollectionTreeNodeSchema.builder().collection(k).parent("Entities"));

                entityNodeBuilder.putExtensions("EXT_collections", EXTCollectionNodeExtensionSchema.builder().addCollections(entityClass).build());
                parentLayer.addChildren(writer.addNode(entityNodeBuilder.build()));
            });

            var extCollections = EXTCollectionExtensionSchema.builder();
            layersCache.forEach((s, builder) -> staticWorldNode.addChildren(writer.addNode(builder.build())));
            groupsCache.forEach((s, builder) -> rootNode.addChildren(writer.addNode(builder.build())));
            collectionCache.forEach((s, builder) -> extCollections.addCollections(builder.build()));

            var khrLightsPunctual = KHRLightsPunctualExtensionSchema.builder().lights(lights).build();
            sceneNodes.add(writer.addNode(rootNode.build()));
            writer.addScene(sceneNodes);
            writer.addExtension("KHR_lights_punctual", khrLightsPunctual);
            writer.addExtension("EXT_collections", extCollections.build());
            writer.addUsedExtension("KHR_lights_punctual", true);
            writer.addUsedExtension("EXT_collections", false);
            writer.write();
        }
    }

    private static Optional<Quaternion> toQuaterion(JsonElement value) {
        if (value == null || value.isJsonNull()) {
            return Optional.empty();
        }
        var mat = value.getAsJsonObject().getAsJsonObject("mat");
        var col0 = toVec3(mat.getAsJsonObject("mat[0]"), 1, 0, 0).orElse(new Vector3(1, 0, 0));
        var col1 = toVec3(mat.getAsJsonObject("mat[1]"), 0, 1, 0).orElse(new Vector3(0, 1, 0));
        var col2 = toVec3(mat.getAsJsonObject("mat[2]"), 0, 0, 1).orElse(new Vector3(0, 0, 1));
        return Optional.of(new Matrix3(
            col0.x(), col0.y(), col0.z(),
            col1.x(), col1.y(), col1.z(),
            col2.x(), col2.y(), col2.z()
        ).rotation());
    }

    private static void addTranslation(NodeSchema.Builder builder, JsonElement value) {
        var vec3 = toVec3(value);
        if (vec3.isPresent()) {
            builder.translation(vec3);
        }
    }

    private static Optional<Vector3> toVec3(JsonElement value) {
        return toVec3(value, 0.0F, 0.0F, 0.0F);
    }

    private static Optional<Vector3> toVec3(JsonElement value, float dx, float dy, float dz) {
        if (value == null || value.isJsonNull()) {
            return Optional.empty();
        }
        var vec = value.getAsJsonObject();
        return Optional.of(new Vector3(
            vec.has("x") ? vec.get("x").getAsNumber().floatValue() : dx,
            vec.has("y") ? vec.get("y").getAsNumber().floatValue() : dy,
            vec.has("z") ? vec.get("z").getAsNumber().floatValue() : dz
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