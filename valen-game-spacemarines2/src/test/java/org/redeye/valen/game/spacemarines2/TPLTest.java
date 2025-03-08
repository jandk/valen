package org.redeye.valen.game.spacemarines2;

import be.twofold.valen.core.export.*;
import be.twofold.valen.core.game.*;
import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.scene.*;
import be.twofold.valen.core.texture.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.export.gltf.*;
import be.twofold.valen.export.png.*;
import com.google.gson.*;
import org.junit.jupiter.api.*;
import org.redeye.valen.game.spacemarines2.psSection.*;
import org.redeye.valen.game.spacemarines2.types.*;
import org.redeye.valen.game.spacemarines2.types.lwi.*;

import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;

public class TPLTest {
    static final PngExporter pngExporter = new PngExporter();

    @BeforeAll
    static void setup() throws IOException {
        Files.createDirectories(Path.of("dump"));
    }

    @Test
    void test_TDLexer() throws IOException {
        SpaceMarines2Game game = new SpaceMarines2GameFactory().load(Path.of("D:\\SteamLibrary\\steamapps\\common\\Space Marine 2\\Warhammer 40000 Space Marine 2.exe"));
        var archive = game.loadArchive("client_pc");
        archive.assets()
            .filter(asset -> asset.id().fileName().endsWith(".td"))
            .forEach(asset -> {
                try {
                    System.out.println(asset.id());
                    var rawData = archive.loadAsset(asset.id(), byte[].class);
                    var res = PsSectionAscii.parseFromString(new String(rawData, StandardCharsets.UTF_8));
                    System.out.println(res);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
    }

    @Test
    void testRead_acid_bomb_01_anim() throws IOException {
        SpaceMarines2Game game = new SpaceMarines2GameFactory().load(Path.of("D:\\SteamLibrary\\steamapps\\common\\Space Marine 2\\Warhammer 40000 Space Marine 2.exe"));
        var archive = game.loadArchive("client_pc");

        EmperorAssetId resourceId = new EmperorAssetId("tpl/acid_bomb_01_anim.tpl/acid_bomb_01_anim.tpl.resource");
        exportModel(archive, Path.of("dump"), resourceId);
    }

    @Test
    void testRead_cc_gargoyle() throws IOException {
        SpaceMarines2Game game = new SpaceMarines2GameFactory().load(Path.of("D:\\SteamLibrary\\steamapps\\common\\Space Marine 2\\Warhammer 40000 Space Marine 2.exe"));
        var archive = game.loadArchive("client_pc");

        EmperorAssetId resourceId = new EmperorAssetId("tpl/cc_gargoyle.tpl/cc_gargoyle.tpl.resource");
        exportModel(archive, Path.of("dump"), resourceId);
    }

    @Test
    void testRead_am_chimera_rig() throws IOException {
        SpaceMarines2Game game = new SpaceMarines2GameFactory().load(Path.of("D:\\SteamLibrary\\steamapps\\common\\Space Marine 2\\Warhammer 40000 Space Marine 2.exe"));

        var archive = game.loadArchive("client_pc");

        EmperorAssetId resourceId = new EmperorAssetId("tpl/am_chimera_rig.tpl/am_chimera_rig.tpl.resource");
        exportModel(archive, Path.of("dump"), resourceId);
    }

    @Test
    void testRead_cc_sporocyst() throws IOException {
        SpaceMarines2Game game = new SpaceMarines2GameFactory().load(Path.of("D:\\SteamLibrary\\steamapps\\common\\Space Marine 2\\Warhammer 40000 Space Marine 2.exe"));
        var archive = game.loadArchive("client_pc");

        EmperorAssetId resourceId = new EmperorAssetId("tpl/cc_sporocyst.tpl/cc_sporocyst.tpl.resource");
        exportModel(archive, Path.of("dump"), resourceId);
    }

    @Test
    void testRead_cc_calgar() throws IOException {
        SpaceMarines2Game game = new SpaceMarines2GameFactory().load(Path.of("D:\\SteamLibrary\\steamapps\\common\\Space Marine 2\\Warhammer 40000 Space Marine 2.exe"));
        var archive = game.loadArchive("client_pc");

        EmperorAssetId resourceId = new EmperorAssetId("tpl/cc_calgar.tpl/cc_calgar.tpl.resource");
        exportModel(archive, Path.of("dump"), resourceId);

    }

    @Test
    void testRead_cc_dreadnought() throws IOException {
        SpaceMarines2Game game = new SpaceMarines2GameFactory().load(Path.of("D:\\SteamLibrary\\steamapps\\common\\Space Marine 2\\Warhammer 40000 Space Marine 2.exe"));
        var archive = game.loadArchive("client_pc");

        EmperorAssetId resourceId = new EmperorAssetId("tpl/cc_dreadnought.tpl/cc_dreadnought.tpl.resource");
        exportModel(archive, Path.of("dump"), resourceId);

    }

    @Test
    void testRead_cc_hive_tyrant_clean() throws IOException {
        SpaceMarines2Game game = new SpaceMarines2GameFactory().load(Path.of("D:\\SteamLibrary\\steamapps\\common\\Space Marine 2\\Warhammer 40000 Space Marine 2.exe"));
        var archive = game.loadArchive("client_pc");

        EmperorAssetId resourceId = new EmperorAssetId("tpl/cc_hive_tyrant_clean.tpl/cc_hive_tyrant_clean.tpl.resource");
        exportModel(archive, Path.of("dump"), resourceId);

    }

    @Test
    void testRead_cc_lord_of_change() throws IOException {
        SpaceMarines2Game game = new SpaceMarines2GameFactory().load(Path.of("D:\\SteamLibrary\\steamapps\\common\\Space Marine 2\\Warhammer 40000 Space Marine 2.exe"));
        var archive = game.loadArchive("client_pc");

        EmperorAssetId resourceId = new EmperorAssetId("tpl/cc_lord_of_change.tpl/cc_lord_of_change.tpl.resource");
        exportModel(archive, Path.of("dump"), resourceId);

    }

    @Test
    void testRead_cc_sm_constructor_victrix_guard() throws IOException {
        SpaceMarines2Game game = new SpaceMarines2GameFactory().load(Path.of("D:\\SteamLibrary\\steamapps\\common\\Space Marine 2\\Warhammer 40000 Space Marine 2.exe"));
        var archive = game.loadArchive("client_pc");

        EmperorAssetId resourceId = new EmperorAssetId("tpl/cc_sm_constructor_victrix_guard.tpl/cc_sm_constructor_victrix_guard.tpl.resource");
        exportModel(archive, Path.of("dump"), resourceId);

    }

    @Test
    void testRead_cc_carnifex() throws IOException {
        SpaceMarines2Game game = new SpaceMarines2GameFactory().load(Path.of("D:\\SteamLibrary\\steamapps\\common\\Space Marine 2\\Warhammer 40000 Space Marine 2.exe"));
        var archive = game.loadArchive("client_pc");

        EmperorAssetId resourceId = new EmperorAssetId("tpl/cc_carnifex.tpl/cc_carnifex.tpl.resource");
        exportModel(archive, Path.of("dump"), resourceId);

    }

    @Test
    void testRead_cc_sorcerer_exalted() throws IOException {
        SpaceMarines2Game game = new SpaceMarines2GameFactory().load(Path.of("D:\\SteamLibrary\\steamapps\\common\\Space Marine 2\\Warhammer 40000 Space Marine 2.exe"));
        var archive = game.loadArchive("client_pc");

        EmperorAssetId resourceId = new EmperorAssetId("tpl/cc_sorcerer_exalted.tpl/cc_sorcerer_exalted.tpl.resource");
        exportModel(archive, Path.of("dump"), resourceId);

    }

    @Test
    void testRead_cine_calgar() throws IOException {
        SpaceMarines2Game game = new SpaceMarines2GameFactory().load(Path.of("D:\\SteamLibrary\\steamapps\\common\\Space Marine 2\\Warhammer 40000 Space Marine 2.exe"));
        var archive = game.loadArchive("client_pc");

        EmperorAssetId resourceId = new EmperorAssetId("tpl/cine_calgar.tpl/cine_calgar.tpl.resource");
        exportModel(archive, Path.of("dump"), resourceId);
    }

    @Test
    void testRead_thunderhawk_body() throws IOException {
        SpaceMarines2Game game = new SpaceMarines2GameFactory().load(Path.of("D:\\SteamLibrary\\steamapps\\common\\Space Marine 2\\Warhammer 40000 Space Marine 2.exe"));
        var archive = game.loadArchive("client_pc");

        EmperorAssetId resourceId = new EmperorAssetId("tpl/thunderhawk_body.tpl/thunderhawk_body.tpl.resource");
        exportModel(archive, Path.of("dump"), resourceId);
    }

    @Test
    void testRead_scripted_turret() throws IOException {
        SpaceMarines2Game game = new SpaceMarines2GameFactory().load(Path.of("D:\\SteamLibrary\\steamapps\\common\\Space Marine 2\\Warhammer 40000 Space Marine 2.exe"));
        var archive = game.loadArchive("client_pc");

        EmperorAssetId resourceId = new EmperorAssetId("tpl/scripted_turret.tpl/scripted_turret.tpl.resource");
        exportModel(archive, Path.of("dump"), resourceId);
    }

    @Test
    void testRead_scene() throws IOException {
        SpaceMarines2Game game = new SpaceMarines2GameFactory().load(Path.of("D:\\SteamLibrary\\steamapps\\common\\Space Marine 2\\Warhammer 40000 Space Marine 2.exe"));
        var archive = game.loadArchive("client_pc");

        EmperorAssetId resourceId = new EmperorAssetId("scenes/story_tower.scn/story_tower.lg");
        var model = archive.loadAsset(resourceId, Model.class);
        String mdlName = resourceId.fileName().substring(0, resourceId.fileName().indexOf('.'));
        var outputPath = Path.of("dump");
        outputPath = outputPath.resolve(mdlName);
        Files.createDirectories(outputPath);
        saveModel(mdlName, (Model) model, outputPath);
    }

    @Test
    void testRead_terrain() throws IOException {
        SpaceMarines2Game game = new SpaceMarines2GameFactory().load(Path.of("D:\\SteamLibrary\\steamapps\\common\\Space Marine 2\\Warhammer 40000 Space Marine 2.exe"));
        var archive = game.loadArchive("client_pc");

        EmperorAssetId resourceId = new EmperorAssetId("scenes/story_blackstone_2.scn/terrain/terrain.terrain");
        var model = archive.loadAsset(resourceId, Model.class);
        String mdlName = resourceId.fileName().substring(0, resourceId.fileName().indexOf('.'));
        var outputPath = Path.of("dump");
        outputPath = outputPath.resolve(mdlName);
        Files.createDirectories(outputPath);
        System.out.println(model);
        // saveModel(mdlName, (Model) model, outputPath);
    }

    @Test
    void testRead_class_list() throws IOException {
        SpaceMarines2Game game = new SpaceMarines2GameFactory().load(Path.of("D:\\SteamLibrary\\steamapps\\common\\Space Marine 2\\Warhammer 40000 Space Marine 2.exe"));
        var archive = game.loadArchive("client_pc");

        EmperorAssetId resourceId = new EmperorAssetId("scenes/story_tower.scn/story_tower.class_list");
        var data = (List<ScnInstanceClassData>) archive.loadAsset(resourceId, List.class);
        // System.out.println(data);
    }

    @Test
    void testRead_cd_list() throws IOException {
        SpaceMarines2Game game = new SpaceMarines2GameFactory().load(Path.of("D:\\SteamLibrary\\steamapps\\common\\Space Marine 2\\Warhammer 40000 Space Marine 2.exe"));
        var archive = game.loadArchive("client_pc");

        EmperorAssetId resourceId = new EmperorAssetId("scenes/story_tower.scn/story_tower.cd_list");
        var data = (List<SceneInstanceCreateData>) archive.loadAsset(resourceId, List.class);
        // System.out.println(data);
    }

    @Test
    void testRead_lwi_inst() throws IOException {
        SpaceMarines2Game game = new SpaceMarines2GameFactory().load(Path.of("D:\\SteamLibrary\\steamapps\\common\\Space Marine 2\\Warhammer 40000 Space Marine 2.exe"));
        var archive = game.loadArchive("client_pc");

        EmperorAssetId resourceId = new EmperorAssetId("scenes/story_tower.scn/story_tower.lwi_inst");
        var data = archive.loadAsset(resourceId, StaticInstanceData.class);
        System.out.println(data);
    }

    @Test
    void testRead_lwi_container() throws IOException {
        SpaceMarines2Game game = new SpaceMarines2GameFactory().load(Path.of("D:\\SteamLibrary\\steamapps\\common\\Space Marine 2\\Warhammer 40000 Space Marine 2.exe"));
        var archive = game.loadArchive("client_pc");

        EmperorAssetId resourceId = new EmperorAssetId("scenes/story_blackstone_2.scn/story_blackstone_2.lwi_container");
        var data = archive.loadAsset(resourceId, LwiContainerStatic.class);
        var modelInfos = data.modelList();

        var instances = new ArrayList<Instance>();
        var alreadyLoaded = new HashMap<EmperorAssetId, Model>();

        for (LwiElementData instanceGroup : data.instanceGroups()) {
            var modelInfo = modelInfos.get(instanceGroup.elemId());
            EmperorAssetId identifier = new EmperorAssetId("tpl/%s.tpl/%s.tpl".formatted(modelInfo.tplName(), modelInfo.tplName()));
            Check.state(archive.exists(identifier));


            for (LwiElementDataChild modelInstance : instanceGroup.instances()) {
                // if (child.subItems().isEmpty()) {
                var matrix = modelInstance.mat();
                Instance instance = new Instance(
                    new ModelReference(modelInfo.tplName(), () -> {
                        var model = alreadyLoaded.computeIfAbsent(identifier, assetId -> {
                            System.out.println(assetId);
                            try {
                                return archive.loadAsset(identifier, Model.class);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        });
                        // if (model.meshes().isEmpty()) {
                        //     System.out.println("Skipped " + modelInfo);
                        //     return null;
                        // }
                        return model;
                    }),
                    matrix.toTranslation(),
                    matrix.toRotation().normalize(),
                    matrix.toScale(),
                    modelInfo.name()
                );

                instances.add(instance);
                // } else {
                //     for (LwiElementDataChildSubItem subItem : child.subItems()) {
                //         var matrix = subItem.mat().multiply(child.mat());
                //         var model = alreadyLoaded.computeIfAbsent(identifier, assetId -> {
                //             System.out.println(assetId);
                //             try {
                //                 return (Model) archive.loadAsset(identifier);
                //             } catch (IOException e) {
                //                 throw new RuntimeException(e);
                //             }
                //         });
                //         Instance instance = new Instance(
                //             meshInfo.name(),
                //             new ModelReference(meshInfo.tplName(), () -> model), matrix.toTranslation(), matrix.toRotation().normalize(), matrix.toScale()
                //         );
                //         instances.add(instance);
                //
                //     }
                // }
            }
        }
        System.out.println("Exported scene with " + instances.size() + " instances");
        var scene = new Scene(instances);
        EmperorAssetId withoutExt = resourceId.withExtension("");
        Path outputPath = Path.of("dump").resolve(withoutExt.fileName());
        Files.createDirectories(outputPath);
        GltfSceneExporter glbSceneExporter = new GltfSceneExporter();
        try (OutputStream outputStream = Files.newOutputStream(outputPath.resolve(withoutExt.fileName() + "." + glbSceneExporter.getExtension()))) {
            glbSceneExporter.export(scene, outputStream);
        }
        System.out.println(1);

    }

    @Test
    void testRead_usable_mine() throws IOException {
        SpaceMarines2Game game = new SpaceMarines2GameFactory().load(Path.of("D:\\SteamLibrary\\steamapps\\common\\Space Marine 2\\Warhammer 40000 Space Marine 2.exe"));
        var archive = game.loadArchive("client_pc");

        EmperorAssetId resourceId = new EmperorAssetId("tpl/usable_mine.tpl/usable_mine.tpl.resource");
        exportModel(archive, Path.of("dump"), resourceId);
    }

    @Test
    void testRead_nuke_expl() throws IOException {
        SpaceMarines2Game game = new SpaceMarines2GameFactory().load(Path.of("D:\\SteamLibrary\\steamapps\\common\\Space Marine 2\\Warhammer 40000 Space Marine 2.exe"));
        var archive = game.loadArchive("client_pc");

        EmperorAssetId resourceId = new EmperorAssetId("tpl/nuke_expl.tpl/nuke_expl.tpl.resource");
        exportModel(archive, Path.of("dump"), resourceId);
    }


    private void exportModel(Archive archive, Path outputPath, EmperorAssetId tplPath) throws IOException {
        EmperorAssetId tplId = tplPath.withExtension("");
        String mdlName = tplId.fileName().substring(0, tplId.fileName().indexOf('.'));
        outputPath = outputPath.resolve(mdlName);
        Files.createDirectories(outputPath);


        var rawData = archive.loadAsset(tplId, byte[].class);
        Files.write(outputPath.resolve(tplId.fileName()), rawData);
        JsonObject resInfo = archive.loadAsset(tplPath, JsonObject.class);
        for (JsonElement materialLink : resInfo.getAsJsonArray("linksTd")) {
            JsonObject matInfo = archive.loadAsset(new EmperorAssetId(materialLink.getAsString().substring(6)), JsonObject.class);
            for (JsonElement textureLink : (matInfo.getAsJsonArray("linksPct"))) {
                String textureLinkString = textureLink.getAsString();
                Texture texture = archive.loadAsset(new EmperorAssetId(textureLinkString.substring(6)), Texture.class);
                var outName = textureLinkString.substring(10, textureLinkString.length() - 13);
                Path pngPath = outputPath.resolve(outName + ".png");
                if (!Files.exists(pngPath)) {
                    try (OutputStream outputStream = Files.newOutputStream(pngPath)) {
                        pngExporter.export(texture, outputStream);
                    }
                }
            }
        }
        Model model = archive.loadAsset(tplId, Model.class);
        saveModel(mdlName, model, outputPath);
    }

    private static void saveModel(String name, Model model, Path outputPath) throws IOException {
        // Exporter<Model> exporter = new DmfModelExporter();
        Exporter<Model> exporter = new GltfModelExporter();
        try (OutputStream outputStream = Files.newOutputStream(outputPath.resolve(name + "." + exporter.getExtension()))) {
            exporter.export(model, outputStream);
        }
    }

}
