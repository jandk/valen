package org.redeye.valen.game.spacemarines2;

import be.twofold.valen.core.geometry.*;
import be.twofold.valen.export.gltf.*;
import be.twofold.valen.export.png.*;
import org.junit.jupiter.api.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class TPLtest {

    @Test
    void testReadAcidBomb() throws IOException {
        SpaceMarines2Game game = new SpaceMarines2GameFactory().load(Path.of("D:\\SteamLibrary\\steamapps\\common\\Space Marine 2\\Warhammer 40000 Space Marine 2.exe"));

        var archive = game.loadArchive("client_pc");
        var rawData = archive.loadRawAsset(new EmperorAssetId("tpl/acid_bomb_01_anim.tpl/acid_bomb_01_anim.tpl"));
        Files.write(Path.of("acid_bomb_01_anim.tpl"), rawData.array());
        List<Model> models = (List<Model>) archive.loadAsset(new EmperorAssetId("tpl/acid_bomb_01_anim.tpl/acid_bomb_01_anim.tpl"));
        for (int i = 0; i < models.size(); i++) {
            Model model = models.get(i);
            try (OutputStream outputStream = Files.newOutputStream(Path.of("test%d.glb".formatted(i)))) {
                new GlbModelExporter().export(model, outputStream);
            }
        }

    }

    @Test
    void testReadGargoyle() throws IOException {
        SpaceMarines2Game game = new SpaceMarines2GameFactory().load(Path.of("D:\\SteamLibrary\\steamapps\\common\\Space Marine 2\\Warhammer 40000 Space Marine 2.exe"));

        var archive = game.loadArchive("client_pc");

        var rawData = archive.loadRawAsset(new EmperorAssetId("tpl/cc_gargoyle.tpl/cc_gargoyle.tpl"));
        Files.write(Path.of("cc_gargoyle.tpl"), rawData.array());
        List<Model> models = (List<Model>) archive.loadAsset(new EmperorAssetId("tpl/cc_gargoyle.tpl/cc_gargoyle.tpl"));
        for (int i = 0; i < models.size(); i++) {
            Model model = models.get(i);
            try (OutputStream outputStream = Files.newOutputStream(Path.of("test%d.glb".formatted(i)))) {
                new GlbModelExporter().export(model, outputStream);
            }
        }
    }

    @Test
    void testReadAm_chimera_rig() throws IOException {
        SpaceMarines2Game game = new SpaceMarines2GameFactory().load(Path.of("D:\\SteamLibrary\\steamapps\\common\\Space Marine 2\\Warhammer 40000 Space Marine 2.exe"));

        var archive = game.loadArchive("client_pc");

        var rawData = archive.loadRawAsset(new EmperorAssetId("tpl/am_chimera_rig.tpl/am_chimera_rig.tpl"));
        Files.write(Path.of("cc_gargoyle.tpl"), rawData.array());
        List<Model> models = (List<Model>) archive.loadAsset(new EmperorAssetId("tpl/am_chimera_rig.tpl/am_chimera_rig.tpl"));
        for (int i = 0; i < models.size(); i++) {
            Model model = models.get(i);
            try (OutputStream outputStream = Files.newOutputStream(Path.of("test%d.glb".formatted(i)))) {
                new GlbModelExporter().export(model, outputStream);
            }
        }
    }

    @Test
    void testRead_cc_sporocyst() throws IOException {
        SpaceMarines2Game game = new SpaceMarines2GameFactory().load(Path.of("D:\\SteamLibrary\\steamapps\\common\\Space Marine 2\\Warhammer 40000 Space Marine 2.exe"));

        var archive = game.loadArchive("client_pc");

        EmperorAssetId fid = new EmperorAssetId("tpl/cc_sporocyst.tpl/cc_sporocyst.tpl");
        var rawData = archive.loadRawAsset(fid);
        Files.write(Path.of(fid.fileName()), rawData.array());
        List<Model> models = (List<Model>) archive.loadAsset(fid);
        for (int i = 0; i < models.size(); i++) {
            Model model = models.get(i);
            try (OutputStream outputStream = Files.newOutputStream(Path.of(model.name()))) {
                new GlbModelExporter().export(model, outputStream);
            }
        }
    }

    @Test
    void testRead_cc_calgar() throws IOException {
        var pngExporter = new PngExporter();

        SpaceMarines2Game game = new SpaceMarines2GameFactory().load(Path.of("D:\\SteamLibrary\\steamapps\\common\\Space Marine 2\\Warhammer 40000 Space Marine 2.exe"));

        var archive = game.loadArchive("client_pc");

        var rawData = archive.loadRawAsset(new EmperorAssetId("tpl/cc_calgar.tpl/cc_calgar.tpl"));
        Files.write(Path.of("cc_calgar.tpl"), rawData.array());
        // Map<String, Map> resInfo = (Map<String, Map>) archive.loadAsset(new EmperorAssetId("tpl/cc_calgar.tpl/cc_calgar.tpl.resource"));
        // for (String materialLink : ((List<String>) resInfo.get("linksTd"))) {
        //     Map<String, Map> matInfo = (Map<String, Map>) archive.loadAsset(new EmperorAssetId(materialLink.substring(6)));
        //     for (String textureLink : ((List<String>) matInfo.get("linksPct"))) {
        //         Texture texture = (Texture) archive.loadAsset(new EmperorAssetId(textureLink.substring(6)));
        //         var outName = textureLink.substring(10, textureLink.length() - 13);
        //         try (OutputStream outputStream = Files.newOutputStream(Path.of(outName + ".png"))) {
        //             pngExporter.export(texture, outputStream);
        //         }
        //     }
        // }

        List<Model> models = (List<Model>) archive.loadAsset(new EmperorAssetId("tpl/cc_calgar.tpl/cc_calgar.tpl"));
        for (int i = 0; i < models.size(); i++) {
            Model model = models.get(i);
            try (OutputStream outputStream = Files.newOutputStream(Path.of(model.name() + ".glb"))) {
                new GlbModelExporter().export(model, outputStream);
            }
        }
    }

}
