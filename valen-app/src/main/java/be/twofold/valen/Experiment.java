package be.twofold.valen;

import be.twofold.valen.core.geometry.Model;
import be.twofold.valen.core.util.*;
import be.twofold.valen.export.gltf.*;
import be.twofold.valen.manager.*;
import be.twofold.valen.oodle.*;
import be.twofold.valen.reader.compfile.*;
import be.twofold.valen.reader.compfile.entities.*;
import be.twofold.valen.resource.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public final class Experiment {
    private static final Set<String> BlackList = Set.of(
            "editors/models/gui_text.lwo",
            "models/ca/working/bshore/darrow1.lwo",
            "models/guis/gui_square.lwo",
            "models/guis/gui_square_afterpost.lwo"
    );

    static final Path HOME = Path.of(System.getProperty("user.home"));
    static final Path BASE = Path.of("D:\\SteamLibrary\\steamapps\\common\\DOOMEternal\\base");

    private final FileManager fileManager;

    public Experiment(FileManager fileManager) {
        this.fileManager = fileManager;
    }

    record StaticGeometryEntry(
            int id,
            String name
    ) {
        public static StaticGeometryEntry read(BetterBuffer buffer) {
            var id = buffer.getInt();
            var name = buffer.getString();
            return new StaticGeometryEntry(id, name);
        }
    }

    record StaticGeometryGeom(
            float[][] floats, int[] indices
    ) {
        public static StaticGeometryGeom read(BetterBuffer buffer) {
            var tempFloats = buffer.getFloats(44);
            var floats = new float[4][11];
            for (var y = 0; y < 11; y++) {
                for (var x = 0; x < 4; x++) {
                    floats[x][y] = tempFloats[y * 4 + x];
                }
            }

            var indices = buffer.getInts(4);

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
        public static StaticGeometryTree read(BetterBuffer buffer) {
            var unknown1 = buffer.getInt();
            var name = buffer.getString();
            var unknown2 = buffer.getInt();
            var count1 = buffer.getInt();
            var count2 = buffer.getInt();
            var numEntries = buffer.getInt();
            var entries = buffer.getStructs(numEntries, StaticGeometryEntry::read);

            var numGeos = buffer.getInt();
            var geos = buffer.getStructs(numGeos, StaticGeometryGeom::read);

            return new StaticGeometryTree(unknown1, name, unknown2, count1, count2, numEntries, entries, geos);
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println(OodleDecompressor.version());

        var manager = new FileManager(BASE);
        manager.select("common");

//        Resource decl = manager.getEntries().stream()
//            .filter(e -> e.name().name().equals("generated/decls/material2/models/weapons/ammo/rocket.decl"))
//            .findFirst().orElseThrow();

//        Resource resource = manager.getEntries().stream()
//                .filter(e -> e.name().name().contains(".entities"))
//                .findFirst().orElseThrow();
//
//        byte[] bytes = manager.readRawResource(resource);
//        EntityFile entityFile = new EntityReader(new CompFileReader()).read(BetterBuffer.wrap(bytes), resource);
//        System.out.println(entityFile);
//
//        if (true) {
//            return;
//        }

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
//        var model = manager.readResource(FileType.AnimatedModel, "md6/characters/monsters/imp/base/assets/mesh/imp.md6mesh");
//        var skeleton = manager.readResource(FileType.Skeleton, "md6/characters/monsters/imp/base/assets/mesh/imp.md6skl");
//        var animation = manager.readResource(FileType.Animation, "md6/characters/monsters/imp/base/motion/combat/idle.md6anim");
//
//        try (var channel = Files.newByteChannel(Path.of("C:\\Temp\\test.glb"), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)) {
//            new GltfWriter(channel, model, skeleton, animation).write();
//        }

//        var model = manager.readResource(FileType.AnimatedModel, "md6/characters/monsters/imp/base/assets/mesh/imp.md6mesh");
//        var skeleton = manager.readResource(FileType.Skeleton, "md6/characters/monsters/imp/base/assets/mesh/imp.md6skl");
//        System.out.println(skeleton);

//        var model = manager.readResource(FileType.StaticModel, "art/skies/models/dlc/uac_oil_rig/arc_ship_a.lwo$uvlayout_lightmap=1");
//
//        if (true) {
//            return;
//        }

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

        var model = manager.readResource(FileType.AnimatedModel, "md6/characters/monsters/imp/base/assets/mesh/imp.md6mesh", ResourceType.Model);
        var skeleton = manager.readResource(FileType.Skeleton, "md6/characters/monsters/imp/base/assets/mesh/imp.md6skl", ResourceType.Skeleton);

        try (var channel = Files.newByteChannel(Path.of("D:\\projects\\java\\valen\\playground\\test.glb"), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)) {
            GltfWriter writer = new GltfWriter(channel);
            var scene = writer.addScene();
            writer.addSkeletalMesh(model, skeleton, scene);
            writer.write();
        }

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
//                    byte[] compressed = IOUtils.readBytes(channel, resource.size());
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

    private void doStreams() {
        fileManager.getSpec().files().stream()
                .filter(s -> s.endsWith(".streamdb"))
                .forEach(this::doStream);
    }

    private void doStream(String s) {
//        Set<ByteBuffer> buffers = new HashSet<>();
//        try (SeekableByteChannel channel = fileManager.open(s)) {
//            channel.position(0);
//            StreamDb db = StreamDb.read(channel);
//            for (StreamDbEntry entry : db.entries()) {
//                channel.position(entry.offset());
//
//                ByteBuffer buffer = ByteBuffer.allocate(2);
//                channel.read(buffer);
//                buffers.add(buffer.flip());
//            }
//        } catch (IOException e) {
//            throw new UncheckedIOException(e);
//        }
//        System.out.println(s);
//        for (ByteBuffer buffer : buffers) {
//            System.out.println("\t" + Arrays.toString(buffer.array()));
//        }
    }

    private void doImage() throws IOException {
//        fileManager.select("game/dlc/hub/hub");
//        Resource entry = fileManager.getResourceEntry("art/decals/signs/warning_sign_highvoltage_01.tga$bc7srgb$streamed$mtlkind=decalalbedo");
//        BetterBuffer buffer = fileManager.readResource(entry);
//        Image image = new ImageReader(buffer, fileManager, entry).read(true);
//        Texture texture = new ImageToTexture().convert(image);
//        try(SeekableByteChannel channel = Files.newByteChannel(Path.of("C:\\Temp\\warning_sign_highvoltage_01.dds"), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)) {
//            new DdsWriter(channel).write(texture);
//        }
//
//        Surface surface = texture.surfaces().get(0);
//        BC7Decoder decoder = new BC7Decoder(4, 0, 1, 2, 3);
//        byte[] decoded = decoder.decode(surface.data(), surface.width(), surface.height());
//
//        try(SeekableByteChannel channel = Files.newByteChannel(Path.of("C:\\Temp\\warning_sign_highvoltage_01.png"), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)) {
//            PngFormat format = new PngFormat(surface.width(), surface.height(), PngColorType.RgbAlpha);
//            Png png = new Png(format, decoded);
//            new PngWriter(channel).write(png);
//        }
//
//        System.out.println(Arrays.toString(decoder.modeCounts));
//        System.out.println(texture);
    }


    private void doStuff() throws Exception {
//        Resource meshEntry = fileManager.getResourceEntry("md6/characters/humans/doomslayer/base/assets/mesh/doomslayer_cine.md6mesh");
//        BetterBuffer meshBuffer = fileManager.readResource(meshEntry);
//        Md6 md6 = new Md6Reader(meshBuffer, fileManager, meshEntry.hash()).read(true);
//
//        Resource sklEntry = fileManager.getResourceEntry("md6/objects/equipment/pickups/collectible_marine/assets/mesh/collectible_marine_small.md6skl");
//        BetterBuffer sklBuffer = fileManager.readResource(sklEntry);
//        Md6Skeleton skl = new Md6SkeletonReader(sklBuffer).read();
//
//        Resource animEntry = fileManager.getResourceEntry("md6/characters/humans/doomslayer/base/motion/podium/idle_01.md6anim");
//        BetterBuffer animBuffer = fileManager.readResource(animEntry);
//        Md6Anim anim = Md6AnimReader.read(animBuffer);
//
//        assert anim.header().skelName().equals(md6.header().sklName());
//
//        Animation animation = mapAnimation(skl, anim);
//
//        try (FileChannel channel = (FileChannel) Files.newByteChannel(Path.of("C:\\Temp\\test.glb"), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)) {
//            GltfWriter writer = new GltfWriter(channel, md6.meshes(), skl, animation);
//            writer.write();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        if (true) {
//            return;
//        }
//
//        Resource defEntry = fileManager.getResourceEntry("md6def/characters/monsters/imp/base/imp.md6");
//        BetterBuffer defBuffer = fileManager.readResource(defEntry);
//        parseDef(defBuffer);
    }


    private static void exportAll(FileManager manager) throws IOException {
        var root = Path.of("C:\\Temp\\DOOMExtracted");

        for (var map : manager.getSpec().maps()) {
            manager.select(map);
            for (var entry : manager.getEntries()) {
                var path = root.resolve(entry.name().path()).resolve(entry.name().file());

                if (!Files.exists(path)) {
                    System.out.println(entry.name());
                    Files.createDirectories(path.getParent());
                    var raw = manager.readRawResource(entry);
                    Files.write(path, raw);
                }
            }
        }
    }

    private void parseDef(BetterBuffer buffer) {
        var num1 = buffer.getInt();
        var shorts = buffer.getShorts(num1 * 3);
        var num2 = buffer.getInt();
        var entries = buffer.getStructs(num2, DefEntry::read);
        try {
            var csv = toCsv(List.of(), entries, DefEntry.class);
            Files.writeString(Path.of("C:\\Temp\\imp.csv"), csv);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

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
        public static DefEntry read(BetterBuffer buffer) {
            var unknown = buffer.getShort();
            var unk1 = buffer.getFloat();
            var unk2 = buffer.getFloat();
            var unk3 = buffer.getFloat();
            var unk4 = buffer.getFloat();
            var unk5 = buffer.getFloat();
            var unk6 = buffer.getFloat();
            var unk7 = buffer.getFloat();
            var unk8 = buffer.getFloat();
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
////                    BetterBuffer buffer = BetterBuffer.wrap(decompressed);
////                    Md6Skeleton model = new Md6SkeletonReader(buffer).read();
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

    static <T> String toCsv(List<String> names, List<T> values, Class<T> clazz) throws Exception {
        var builder = new StringBuilder();
        var components = clazz.getRecordComponents();
        builder.append("filename");
        for (var component : components) {
            builder.append(',').append(component.getName());
        }
        builder.append('\n');

        for (var i = 0; i < values.size(); i++) {
            var element = values.get(i);
            if (element == null) {
                continue;
            }
            if (names.size() == values.size()) {
                builder.append(names.get(i));
            }
            for (var component : components) {
                var o = component.getAccessor().invoke(element);
                var s = switch (o) {
                    case List<?> ignored -> "";
                    case byte[] bytes -> Arrays.toString(bytes);
                    case short[] shorts -> Arrays.toString(shorts);
                    case int[] ints -> Arrays.toString(ints);
                    case int[][] ints -> Arrays.deepToString(ints);
                    case float[] floats -> Arrays.toString(floats);
                    default -> Objects.toString(o);
                };
                builder.append(',').append(s.contains(",") ? "\"" + s + "\"" : s);
            }
            builder.append('\n');
        }
        return builder.toString();
    }
}
