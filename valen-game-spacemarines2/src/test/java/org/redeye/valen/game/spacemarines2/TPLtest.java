package org.redeye.valen.game.spacemarines2;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.texture.*;
import be.twofold.valen.export.gltf.*;
import be.twofold.valen.export.png.*;
import org.junit.jupiter.api.*;
import org.redeye.valen.game.spacemarines2.td.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class TPLtest {

    static final PngExporter pngExporter = new PngExporter();

    @BeforeAll
    static void setup() throws IOException {
        Files.createDirectories(Path.of("dump"));
    }

    @Test
    void test_TDLexer() throws IOException {
        SpaceMarines2Game game = new SpaceMarines2GameFactory().load(Path.of("D:\\SteamLibrary\\steamapps\\common\\Space Marine 2\\Warhammer 40000 Space Marine 2.exe"));
        var archive = game.loadArchive("client_pc");
        for (Asset asset : archive.assets()) {
            if (asset.id().fileName().endsWith(".td")) {
                System.out.println(asset.id());
                var rawData = archive.loadRawAsset(asset.id());
                TDParser parser = new TDParser(new InputStreamReader(new ByteArrayInputStream(rawData.array())));
                var res = parser.parse();
                System.out.println(res);
            }
        }
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
    void testReadAm_chimera_rig() throws IOException {
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
        var model = archive.loadAsset(resourceId);
        String mdlName = resourceId.fileName().substring(0, resourceId.fileName().indexOf('.'));
        var outputPath = Path.of("dump");
        outputPath = outputPath.resolve(mdlName);
        Files.createDirectories(outputPath);
        saveModel((Model) model, outputPath);
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
        EmperorAssetId tplId = tplPath.withExt("");
        String mdlName = tplId.fileName().substring(0, tplId.fileName().indexOf('.'));
        outputPath = outputPath.resolve(mdlName);
        Files.createDirectories(outputPath);


        var rawData = archive.loadRawAsset(tplId);
        Files.write(outputPath.resolve(tplId.fileName()), rawData.array());

        Map<String, Map> resInfo = (Map<String, Map>) archive.loadAsset(tplPath);
        for (String materialLink : ((List<String>) resInfo.get("linksTd"))) {
            Map<String, Map> matInfo = (Map<String, Map>) archive.loadAsset(new EmperorAssetId(materialLink.substring(6)));
            for (String textureLink : ((List<String>) matInfo.get("linksPct"))) {
                Texture texture = (Texture) archive.loadAsset(new EmperorAssetId(textureLink.substring(6)));
                var outName = textureLink.substring(10, textureLink.length() - 13);
                Path pngPath = outputPath.resolve(outName + ".png");
                if (!Files.exists(pngPath)) {
                    try (OutputStream outputStream = Files.newOutputStream(pngPath)) {
                        pngExporter.export(texture, outputStream);
                    }
                }
            }
        }
        Model model = (Model) archive.loadAsset(tplId);
        saveModel(model, outputPath);
    }

    private static void saveModel(Model model, Path outputPath) throws IOException {
        try (OutputStream outputStream = Files.newOutputStream(outputPath.resolve(model.name() + ".glb"))) {
            new GlbModelExporter().export(model, outputStream);
        }
    }

}
