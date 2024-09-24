package org.redeye.valen.game.spacemarines2;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.texture.*;
import be.twofold.valen.export.gltf.*;
import be.twofold.valen.export.png.*;
import org.junit.jupiter.api.*;

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
    void testReadAcidBomb() throws IOException {
        SpaceMarines2Game game = new SpaceMarines2GameFactory().load(Path.of("D:\\SteamLibrary\\steamapps\\common\\Space Marine 2\\Warhammer 40000 Space Marine 2.exe"));
        var archive = game.loadArchive("client_pc");

        EmperorAssetId resourceId = new EmperorAssetId("tpl/acid_bomb_01_anim.tpl/acid_bomb_01_anim.tpl.resource");
        exportModel(archive, Path.of("dump"), resourceId);
    }

    @Test
    void testReadGargoyle() throws IOException {
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
        EmperorAssetId tplId = tplPath.withExt(".tpl");
        String mdlName = tplId.fileName().substring(0, tplId.fileName().lastIndexOf('.'));
        outputPath = outputPath.resolve(mdlName);
        Files.createDirectories(outputPath);


        var rawData = archive.loadRawAsset(tplPath);
        Files.write(outputPath.resolve(tplId.fileName()), rawData.array());

        Map<String, Map> resInfo = (Map<String, Map>) archive.loadAsset(tplPath);
        for (String materialLink : ((List<String>) resInfo.get("linksTd"))) {
            Map<String, Map> matInfo = (Map<String, Map>) archive.loadAsset(new EmperorAssetId(materialLink.substring(6)));
            for (String textureLink : ((List<String>) matInfo.get("linksPct"))) {
                Texture texture = (Texture) archive.loadAsset(new EmperorAssetId(textureLink.substring(6)));
                var outName = textureLink.substring(10, textureLink.length() - 13);
                try (OutputStream outputStream = Files.newOutputStream(outputPath.resolve(outName + ".png"))) {
                    pngExporter.export(texture, outputStream);
                }
            }
        }
        List<Model> models = (List<Model>) archive.loadAsset(tplId);
        saveModels(models, outputPath);
    }

    private static void saveModels(List<Model> models, Path outputPath) throws IOException {
        for (int i = 0; i < models.size(); i++) {
            Model model = models.get(i);
            try (OutputStream outputStream = Files.newOutputStream(outputPath.resolve(model.name() + ".glb"))) {
                new GlbModelExporter().export(model, outputStream);
            }
        }
    }

}
