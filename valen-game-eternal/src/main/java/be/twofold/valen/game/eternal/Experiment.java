package be.twofold.valen.game.eternal;

import be.twofold.valen.core.animation.*;
import be.twofold.valen.core.game.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.core.texture.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.game.eternal.resource.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public final class Experiment {
    static final Path EXECUTABLE_PATH = Path.of("D:\\Games\\Steam\\steamapps\\common\\DOOMEternal\\DOOMEternalx64vk.exe");

    record StaticGeometryEntry(
        int id,
        String name
    ) {
        public static StaticGeometryEntry read(DataSource source) throws IOException {
            var id = source.readInt();
            var name = source.readPString();
            return new StaticGeometryEntry(id, name);
        }
    }

    record StaticGeometryGeom(
        float[][] floats, int[] indices
    ) {
        public static StaticGeometryGeom read(DataSource source) throws IOException {
            var tempFloats = source.readFloats(44);
            var floats = new float[4][11];
            for (var y = 0; y < 11; y++) {
                for (var x = 0; x < 4; x++) {
                    floats[x][y] = tempFloats[y * 4 + x];
                }
            }

            var indices = source.readInts(4);

            return new StaticGeometryGeom(floats, indices);
        }
    }

    record StaticGeometryTree(
        int unknown1,
        String name,
        int unknown2,
        int count1,
        int count2,
        int numEntries,
        List<StaticGeometryEntry> entries,
        List<StaticGeometryGeom> geos
    ) {
        public static StaticGeometryTree read(DataSource source) throws IOException {
            var unknown1 = source.readInt();
            var name = source.readPString();
            var unknown2 = source.readInt();
            var count1 = source.readInt();
            var count2 = source.readInt();
            var numEntries = source.readInt();
            var entries = source.readStructs(numEntries, StaticGeometryEntry::read);

            var numGeos = source.readInt();
            var geos = source.readStructs(numGeos, StaticGeometryGeom::read);

            return new StaticGeometryTree(unknown1, name, unknown2, count1, count2, numEntries, entries, geos);
        }
    }

    public static void main(String[] args) throws Exception {
//        var compressed = Files.readAllBytes(Path.of("D:\\Jan\\Desktop\\Untitled2"));
//        var decompressed = Compression.Kraken.decompress(ByteBuffer.wrap(compressed), 7226);
//        Files.write(Path.of("D:\\Jan\\Desktop\\Untitled2.bin"), Buffers.toArray(decompressed));

        if (true) {
            return;
        }

        var game = new EternalGameFactory().load(EXECUTABLE_PATH);
        exportAll(game);
        if (true) {
            return;
        }

        var archive = game.loadArchive("game/sp/e1m1_intro/e1m1_intro");

        var name = "models/customization/characters/doomslayer/set49/slayer_legs_set49_hq_e.tga$bc1srgb$minmip=1$streamed$mtlkind=bloommask";
        var image = (Texture) archive.loadAsset(ResourceKey.from(name, ResourceType.Image));
//
//        List<Exporter<Texture>> exporters = Exporters.forType(Texture.class);
//        var exporter = exporters.stream()
//            .filter(e -> e.getExtension().equals("dds"))
//            .findFirst().orElseThrow();
//
//        try(var out = Files.newOutputStream(Path.of("D:\\Eternal\\slayer_legs_set49_hq_e.dds"))) {
//            exporter.export(image, out);
//        }

        if (true) return;
//
//        //        var texture = manager.readResource("md6/characters/monsters/darklord_mech/base/assets/mesh/darklord_mech.md6mesh", FileType.AnimatedModel);
//        Model model = manager.readResource("md6/characters/monsters/darklord_mech/base/assets/mesh/darklord_mech.md6mesh", FileType.AnimatedModel);
//        var animations = manager.getEntries().stream()
//            .filter(e -> e.type() == ResourceType.Anim && e.name().name().startsWith("md6/characters/monsters/darklord_mech/base/motion/"))
//            .toList();
//
//        var animationList = new ArrayList<Animation>();
//        for (Resource entry : animations) {
//            Animation animation = manager
//                .readResource(entry.nameString(), FileType.Animation)
//                .withName(entry.nameString().replace("md6/characters/monsters/darklord_mech/base/motion/", ""));
//
//            animationList.add(animation);
//        }
//
//        animationList.sort(Comparator.comparing(Animation::name));
//        for (int i = 0; i < animationList.size(); i++) {
//            Animation animation = animationList.get(i);
//            if (!checkAnimation(animation, model.skeleton().bones().size())) {
//                System.out.println("Animation " + animation.name() + " (" + i + ") has invalid bone count");
//            }
//        }
//
//        model = model.withAnimations(animationList);
//
//        System.out.println(animations.size());
//        // System.out.println(model);
//
//        List<Exporter<Model>> exporters = Exporters.forType(Model.class);
//        var exporter = exporters.stream()
//            .filter(e -> e.getExtension().equals("glb"))
//            .findFirst().orElseThrow();
//
//        try (var out = Files.newOutputStream(Path.of("D:\\Eternal\\darklord_mech.glb"))) {
//            exporter.export(model, out);
//        }

        exportAll(game);

        if (true) {
            return;
        }

//        byte[] bytes = Files.readAllBytes(Path.of("D:\\Jan\\Desktop\\tutorial_sp.bsgstree"));
//        StaticGeometryTree tree = StaticGeometryTree.read(BetterBuffer.wrap(bytes));
//
//        Map<Integer, Set<Float>> map = new HashMap<>();
//        for (StaticGeometryGeom geo : tree.geos()) {
//            for (int i = 0; i < 4; i++) {
//                float[][] floats = geo.floats();
//
//                float dx = Math.abs(floats[i][0] - floats[i][3]);
//                float dy = Math.abs(floats[i][1] - floats[i][4]);
//                float dz = Math.abs(floats[i][2] - floats[i][5]);
//                float volume = (dx * dy * dz);
//
//                System.out.println(volume / floats[i][10]);
//
//                map.computeIfAbsent(geo.indices()[i], k -> new HashSet<>()).add(floats[i][10]);
//            }
//        }
//
//        map.entrySet().forEach(System.out::println);
//
//
//        List<StaticGeometryGeom> geoms = new ArrayList<>();
//        String csv1 = toCsv(List.of(), tree.geos, StaticGeometryGeom.class);
//        Files.writeString(Path.of("C:\\Temp\\CSV\\static_geometry_geoms.csv"), csv1);

//

        // var model = manager.readResource("md6/characters/monsters/imp/base/assets/mesh/imp.md6mesh", FileType.AnimatedModel);
        // var skeleton = manager.readResource(FileType.Skeleton, "md6/characters/humans/doomslayer/base/assets/mesh/doomslayer.md6skl");
        // var animation = manager.readResource(FileType.Animation, "md6/characters/monsters/imp/base/motion/combat/idle.md6anim");

//        var context = new GltfContext();
//        MeshId meshId = context.addMesh(model);
        // var skin = writer.addSkin(model.skeleton());
        // writer.addMeshInstance(mesh, "doomslayer_cine", Quaternion.Identity, Vector3.Zero, Vector3.One);

//        try (var channel = Files.newByteChannel(Path.of("D:\\Eternal\\imp.glb"), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)) {
//            new GlbWriter(context).write(channel);
//        }

//        var model = manager.readResource(FileType.AnimatedModel, "md6/characters/monsters/imp/base/assets/mesh/imp.md6mesh");
//        var skeleton = manager.readResource(FileType.Skeleton, "md6/characters/monsters/imp/base/assets/mesh/imp.md6skl");
//        System.out.println(skeleton);

//        var model = manager.readResource(FileType.StaticModel, "art/skies/models/dlc/uac_oil_rig/arc_ship_a.lwo$uvlayout_lightmap=1");
//
        if (true) {
            return;
        }

//        var bone = skeleton.bones().get(4);
//        System.out.println(bone.name());

//        for (String map : manager.getSpec().maps()) {
//            manager.select(map);
//            exportModels(manager);
//        }

//        manager.getEntries().stream()
//            .filter(e -> e.type() == ResourceType.Image)
//            .filter(e -> e.name().name().startsWith("art/decals/organic/"))
//            .forEach(e -> {
//                System.out.println(e.name());
//                try {
//                    // Export image as dds
//                    var path = Path.of("C:\\Temp\\DOOM Textures").resolve(e.name().path()).resolve(e.name().fileWithoutExtension() + ".dds");
//                    if (Files.exists(path)) {
//                        return;
//                    }
//
//                    var texture = manager.readResource(FileType.Image, e.name().toString());
//                    Files.createDirectories(path.getParent());
//                    try (var channel = Files.newByteChannel(path, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)) {
//                        new DdsWriter(channel).write(texture);
//                    }
//                } catch (IOException ex) {
//                    ex.printStackTrace();
//                }
//            });
//
//        if (true) {
//            return;
//        }


//        Texture texture = manager.readResource(FileType.Image, "art/weapons/heavycannon/heavy_base_back.tga$streamed$mtlkind=albedo");
//        System.out.println(texture);

//        List<Resource> res = manager.getResourceEntries().stream()
//            .filter(r -> r.name().toString().equals("md6/characters/monsters/imp/base/assets/mesh/imp.md6mesh"))
//            .toList();
//
//        int i = 0;
//        for (Resource r : res) {
//            BetterBuffer buffer = manager.readResource(r);
//            Model model = new ModelReader(buffer, manager, r.hash()).read(true);
//
//            try (SeekableByteChannel channel = Files.newByteChannel(Path.of("C:\\Temp\\test.glb"), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)) {
//                new GltfWriter(channel, model.meshes(), null, null).write();
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        }

//        if (true) {
//            return;
//        }

//        Resource entry = manager.getResourceEntry("art/skies/models/dlc/uac_oil_rig/arc_ship_a.lwo$uvlayout_lightmap=1");
//        BetterBuffer buffer = manager.readResource(entry);
//        var lwo = new ModelReader(buffer, manager, entry).read();
//
//        try (SeekableByteChannel channel = Files.newByteChannel(Path.of("C:\\Temp\\arc_ship_a.lwo"), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)) {
//            new GltfWriter(channel, lwo.meshes(), null, null).write();
//        }

//        long t0 = System.nanoTime();
//        List<String> names = new ArrayList<>();
//        List<Md6AnimData> list = new ArrayList<>();
//
//        List<String> files = manager.getSpec().files().stream()
//            .filter(s -> s.endsWith(".resources"))
//            .toList();
//
//        for (String file : files) {
//            try (SeekableByteChannel channel = Files.newByteChannel(BASE.resolve(file))) {
//                List<Resource> resources = new ResourceMapper().map(Resources.read(channel));
//
//                for (Resource resource : resources) {
//                    if (resource.type() != ResourceType.Anim || BlackList.contains(resource.name().toString()) || resource.uncompressedSize() == 0) {
//                        continue;
//                    }
//
//                    names.add(resource.name().toString());
//
//                    channel.position(resource.offset());
//                    byte[] compressed = IOUtils.readBytes(channel, resource.compressedSize());
//                    byte[] decompressed = OodleDecompressor.decompress(compressed, resource.uncompressedSize());
//                    BetterBuffer bb = BetterBuffer.wrap(decompressed);
//
//                    Animation anim = new Md6AnimReader().read(bb, resource);
//                }
//            }
//        }
//        long t1 = System.nanoTime();
//        System.out.printf("Reading took %.1fms%n", (t1 - t0) / 1e6);

        // String csv = toCsv(names, list, Md6AnimData.class);
        // Files.writeString(Path.of("C:\\Temp\\CSV\\md6anim_datas.csv"), csv);

//        Main main = new Main(fileManager);

//        main.doCgr();
//        main.doStuff();
//        main.doResources();
//        main.export();
//        main.doImage();
    }

    private static boolean checkAnimation(Animation animation, int boneCount) {
        return animation.tracks().stream()
            .noneMatch(t -> t.boneId() >= boneCount);
    }

    private static void exportAll(Game game) throws IOException {
        var root = Path.of("D:\\Eternal\\Extracted");

        for (var archiveName : game.archiveNames()) {
            var archive = game.loadArchive(archiveName);
            var assets = archive.assets().stream()
                .sorted()
                .toList();

            for (var asset : assets) {
                var path = root
                    .resolve(((ResourceKey) asset.id()).type().toString())
                    .resolve(asset.id().fullName());
                if (Files.exists(path)) {
                    continue;
                }

                Files.createDirectories(path.getParent());
                var buffer = archive.loadRawAsset(asset.id());
                Files.write(path, Buffers.toArray(buffer));
            }
        }
    }

//    private void parseDef(DataSource source) throws IOException {
//        var num1 = source.readInt();
//        var shorts = source.readShorts(num1 * 3);
//        var num2 = source.readInt();
//        var entries = source.readStructs(num2, DefEntry::read);
//        try {
//            var csv = toCsv(List.of(), entries, DefEntry.class);
//            Files.writeString(Path.of("C:\\Temp\\imp.csv"), csv);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }

    private record DefEntry(
        short unknown,
        float unk1,
        float unk2,
        float unk3,
        float unk4,
        float unk5,
        float unk6,
        float unk7,
        float unk8
    ) {
        public static DefEntry read(DataSource source) throws IOException {
            var unknown = source.readShort();
            var unk1 = source.readFloat();
            var unk2 = source.readFloat();
            var unk3 = source.readFloat();
            var unk4 = source.readFloat();
            var unk5 = source.readFloat();
            var unk6 = source.readFloat();
            var unk7 = source.readFloat();
            var unk8 = source.readFloat();
            return new DefEntry(unknown, unk1, unk2, unk3, unk4, unk5, unk6, unk7, unk8);
        }
    }

    private void doResources() throws Exception {
//        List<String> paths = fileManager.getSpec().files().stream()
//            .filter(s -> s.endsWith(".resources"))
//            .toList();
//
//        List<String> names = new ArrayList<>();
//        List<Md6SkeletonHeader> values = new ArrayList<>();
//        for (String path : paths) {
//            try (SeekableByteChannel channel = Files.newByteChannel(BASE.resolve(path))) {
//                Resources resource = Resources.read(channel);
//                List<Resource> resources = ResourceMapper.map(resource);
//                for (Resource entry : resources) {
////                    if (entry.dataSize() == 0 || !entry.klass().equals("skeleton") || BlackList.contains(entry.name().toString())) {
//////                     if (entry.dataSize() == 0 || !entry.materialName().toString().equals("md6/characters/monsters/zombie_tier1/base/motion/pain/twitch/front/additive/chest_01.md6anim")) {
//////                    if (entry.dataSize() == 0 || !entry.materialName().toString().startsWith("md6/characters/monsters/imp/") || !entry.materialName().toString().endsWith(".md6anim")) {
////                        continue;
////                    }
////                    names.add(entry.name().toString());
////
////                    channel.position(entry.dataOffset());
////                    byte[] compressed = IOUtils.readBytes(channel, entry.dataSize());
////                    byte[] decompressed = OodleDecompressor.decompress(compressed, entry.uncompressedSize());
////                    BetterBuffer source = BetterBuffer.wrap(decompressed);
////                    Md6Skeleton model = new Md6SkeletonReader(source).read();
////                    values.add(model.header());
//                }
//            } catch (IOException e) {
//                throw new UncheckedIOException(e);
//            }
//        }
//
//        String csv = toCsv(names, values, Md6SkeletonHeader.class);
//        Files.writeString(Path.of("C:\\Temp\\CSV\\skeleton_headers.csv"), csv);
    }

}