package be.twofold.valen;

import be.twofold.valen.core.math.Matrix3;
import be.twofold.valen.core.math.Quaternion;
import be.twofold.valen.core.math.Vector3;
import be.twofold.valen.export.gltf.GltfWriter;
import be.twofold.valen.export.gltf.model.*;
import be.twofold.valen.export.gltf.model.extensions.collections.*;
import be.twofold.valen.export.gltf.model.extensions.lightspunctual.*;
import be.twofold.valen.manager.*;
import be.twofold.valen.reader.*;
import be.twofold.valen.reader.compfile.entities.*;
import be.twofold.valen.reader.decl.*;
import be.twofold.valen.reader.staticinstances.*;
import be.twofold.valen.ui.settings.*;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;

import static be.twofold.valen.Experiment.BASE;

public final class MapExporter {

    public static void main(String[] args) throws IOException {
        SettingsManager.get().setGameDirectory(BASE);
        var manager = DaggerManagerFactory.create().fileManager();
        SettingsManager.get().getGameDirectory().ifPresent(manager::load);
        var exporter = new MapExporter(manager);
        exporter.export("game/dlc/hub/hub");
    }

    private static final Set<String> BlackList = Set.of(
        "editors/models/gui_text.lwo",
        "models/ca/working/bshore/darrow1.lwo",
        "models/guis/gui_square.lwo",
        "models/guis/gui_square_afterpost.lwo"
    );
    private final Map<String, MeshId> meshCache = new LinkedHashMap<>();
    private final Map<String, NodeSchema.Builder> layersCache = new LinkedHashMap<>();
    private final Map<String, NodeSchema.Builder> groupsCache = new LinkedHashMap<>();
    private final Map<String, CollectionTreeNodeSchema.Builder> collectionCache = new LinkedHashMap<>();

    private final ArrayList<NodeId> sceneNodes = new ArrayList<>();
    private final List<LightSchema> lights = new ArrayList<>();

    private final FileManager fileManager;

    public MapExporter(FileManager fileManager) {
        this.fileManager = fileManager;
    }

    private void export(String mapPackage) throws IOException {
        meshCache.clear();
        layersCache.clear();
        groupsCache.clear();
        collectionCache.clear();
        sceneNodes.clear();
        lights.clear();

        fileManager.select(mapPackage);

        var instances = fileManager.readResource(FileType.StaticInstances, "generated/staticinstances/maps/" + mapPackage + ".staticinstances");
        var entities = fileManager.readResource(FileType.Entities, "maps/" + mapPackage + ".entities");
//        var md6DefParser = new MD6DefParser();

        try (var channel = Files.newByteChannel(Path.of("map.glb"), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)) {
            var writer = new GltfWriter(channel);

            var rootNode = NodeSchema.builder()
                .name("Root")
                .rotation(new Quaternion(-(float) (Math.sqrt(2) / 2), 0, 0, (float) (Math.sqrt(2) / 2)))
                .scale(new Vector3(0.7f, 0.7f, 0.7f))
                .children(new ArrayList<>());

            collectionCache.put("Root", CollectionTreeNodeSchema.builder().collection("Root"));
            collectionCache.put("Entities", CollectionTreeNodeSchema.builder().collection("Entities").parent("Root"));
            var staticWorldNode = NodeSchema.builder().name("StaticWorld");
            groupsCache.put("StaticWorld", staticWorldNode);

            exportStaticInstances(instances, writer);

            exportEntities(entities, writer);

            var extCollections = EXTCollectionExtensionSchema.builder();
            layersCache.forEach((s, layerNode) -> staticWorldNode.addChildren(writer.addNode(layerNode.build())));
            groupsCache.forEach((s, groupNode) -> rootNode.addChildren(writer.addNode(groupNode.build())));
            collectionCache.forEach((s, collection) -> extCollections.addCollections(collection.build()));

            var khrLightsPunctual = KHRLightsPunctualExtensionSchema.builder().lights(lights);
            sceneNodes.add(writer.addNode(rootNode.build()));
            writer.addScene(sceneNodes);
            writer.addExtension("KHR_lights_punctual", khrLightsPunctual.build(), true);
            writer.addExtension("EXT_collections", extCollections.build(), false);
            writer.write();
        }

    }

    private void exportEntities(EntityFile entities, GltfWriter writer) {
        entities.entities().forEach((entityName, entity) -> {
            var entityData = entity.entityDef();
            var entityEditData = entityData.getAsJsonObject("edit");
            var sourceIOData = new JsonObject();
            var sourceIOEntityData = new JsonObject();
            var asJsonObject = entityEditData.getAsJsonObject();
            asJsonObject.add("classname", entityData.get("class"));
            sourceIOEntityData.add("entity", asJsonObject);
            sourceIOData.add("entity_data", sourceIOEntityData);
            var entityNodeBuilder = NodeSchema.builder()
                .name(entityName)
                .extras(sourceIOData);
            var entityClass = entityData.get("class").getAsString();
            switch (entityClass) {
                case "idLight" -> {
                    handleLight(entityName, entityEditData, entityNodeBuilder);
                }
                case "idGuiEntity", "idDynamicEntity", "idMover" -> {
                    handleStaticModelEntity(writer, entityEditData, entityNodeBuilder);
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
                default -> {
                    System.out.println("Unhandled entity: " + entityClass);
                    entityNodeBuilder.rotation(toQuaternion(entityEditData.get("spawnOrientation")).orElse(new Quaternion(0, 0, 0, 1)));
                    entityNodeBuilder.translation(toVec3(entityEditData.get("spawnPosition")).orElse(new Vector3(0, 0, 0)));
                }
            }


            var parentLayer = groupsCache.computeIfAbsent(entityClass, k -> NodeSchema.builder().name(k));
            collectionCache.computeIfAbsent(entityClass, k -> CollectionTreeNodeSchema.builder().collection(k).parent("Entities"));

            entityNodeBuilder.putExtensions("EXT_collections", EXTCollectionNodeExtensionSchema.builder().addCollections(entityClass).build());
            parentLayer.addChildren(writer.addNode(entityNodeBuilder.build()));
        });
    }

    private void handleStaticModelEntity(GltfWriter writer, JsonObject entityEditData, NodeSchema.Builder entityNodeBuilder) {
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

                var model = fileManager.readResource(FileType.StaticModel, tmp);
                return writer.addMesh(model);
            } catch (IllegalArgumentException ex) {
                System.err.println("Failed to find " + modelName + " model, due to " + ex);
                ex.printStackTrace();
                return null;
            }
        });
        if (meshId != null) {
            entityNodeBuilder.mesh(meshId);
        }

        toVec3(modelInfo.get("scale")).ifPresent(entityNodeBuilder::scale);
        entityNodeBuilder.rotation(toQuaternion(entityEditData.get("spawnOrientation")));
        entityNodeBuilder.translation(toVec3(entityEditData.get("spawnPosition")));
    }

    private void handleLight(String entityName, JsonObject entityEditData, NodeSchema.Builder entityNodeBuilder) {
        var rotation = toQuaternion(entityEditData.get("spawnOrientation")).orElse(new Quaternion(0, 0, 0, 1));
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
        entityNodeBuilder.rotation(rotation);
        entityNodeBuilder.translation(toVec3(entityEditData.get("spawnPosition")));
    }

    private void exportStaticInstances(StaticInstances instances, GltfWriter writer) {
        collectionCache.put("StaticWorld", CollectionTreeNodeSchema.builder().collection("StaticWorld").parent("Root"));
        for (var i = 0; i < instances.modelInstanceGeometries().size(); i++) {
            var geometry = instances.modelInstanceGeometries().get(i);
            var modelName = instances.models().get(geometry.modelIndex()).toLowerCase(Locale.ROOT);
            var meshId = meshCache.computeIfAbsent(modelName, k -> {
                try {
                    var model = fileManager.readResource(FileType.StaticModel, modelName);
                    return writer.addMesh(model);

                } catch (IllegalArgumentException ex) {
                    System.err.println("Failed to find " + modelName + " model, due to " + ex);
                    ex.printStackTrace();
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
    }


    private static Optional<Quaternion> toQuaternion(JsonElement value) {
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


    private static Optional<Vector3> toVec3(JsonElement value, float dx, float dy, float dz) {
        return extractVector3(value, "x", "y", "z", dx, dy, dz);
    }

    private static Optional<Vector3> toVec3(JsonElement value) {
        return extractVector3(value, "x", "y", "z", 0f, 0f, 0f);
    }

    private static Optional<Vector3> toColor(JsonElement value) {
        return extractVector3(value, "r", "g", "b", 1f, 1f, 1f);
    }

    private static Optional<Vector3> extractVector3(JsonElement value,
                                                    String keyX, String keyY, String keyZ,
                                                    float defaultValueX, float defaultValueY, float defaultValueZ) {
        if (value == null || value.isJsonNull()) {
            return Optional.empty();
        }

        JsonObject vec = value.getAsJsonObject();
        return Optional.of(new Vector3(
            vec.has(keyX) ? vec.get(keyX).getAsNumber().floatValue() : defaultValueX,
            vec.has(keyY) ? vec.get(keyY).getAsNumber().floatValue() : defaultValueY,
            vec.has(keyZ) ? vec.get(keyZ).getAsNumber().floatValue() : defaultValueZ
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