package be.twofold.valen.game.doom;

import be.twofold.valen.core.export.*;
import be.twofold.valen.core.texture.*;
import be.twofold.valen.game.doom.mega2.*;
import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.compress.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

public final class Main {
    public static void main(String[] args) throws Exception {
        var path = Path.of("D:\\Games\\Steam\\steamapps\\common\\DOOM\\virtualtextures\\");

        var paths = IntStream.rangeClosed(1, 16)
            .mapToObj(i -> path.resolve(String.format("_vmtr_sq%d.mega2", i)))
            .toList();

        var mega2Files = new ArrayList<Mega2File>();
        for (Path p : paths) {
            mega2Files.add(Mega2File.open(p));
        }

        exportImage(mega2Files, 4);
        if (true) {
            return;
        }

        try (var source = BinarySource.open(path)) {
            var mega2 = Mega2.read(source);

            var pointers = mega2.pointers().stream()
                .sorted(Comparator.comparingLong(Mega2Entry::offset))
                .toList();

            int count = 0;
            long[] modes = new long[8];
            long[] alphas = new long[256];
            for (var pointer : pointers) {
                source.position(pointer.offset());
                Mega2PageHeader pageHeader = Mega2PageHeader.read(source);
                if (pageHeader.colorMaskSize() == 0) {
                    continue;
                }

                source.skip(pageHeader.diffuseSize() + pageHeader.specularSize() + pageHeader.lightmapSize());
                Bytes compressed = source.readBytes(pageHeader.colorMaskSize());
                Bytes decompressed = Decompressor.lz4Block().decompress(compressed, 16384);
                for (int i = 0; i < decompressed.length(); i += 16) {
                    modes[Integer.numberOfTrailingZeros(decompressed.get(i))]++;
                }

                var pixels = new Surface(TextureFormat.BC7_UNORM, 128, 128, 1, decompressed)
                    .toTexture()
                    .convert(TextureFormat.R8G8B8A8_UNORM, false)
                    .getSurface(0, 0).data();

                for (int i = 3; i < pixels.length(); i += 4) {
                    alphas[pixels.getUnsigned(i)]++;
                }

                if (++count % 1000 == 0) {
                    System.out.println("Processed " + count + " pages out of " + mega2.pointers().size());
                }
            }

            System.out.println(Arrays.toString(modes));
            System.out.println(Arrays.toString(alphas));
        }
    }

    private static void exportImage(List<Mega2File> mega2Files, int level) throws IOException {
        var firstFileLevel = mega2Files.getFirst().mega().levels().get(level);
        var target = Surface.create(firstFileLevel.blockXCount() * 120 * 4, firstFileLevel.blockYCount() * 120 * 4, 1, TextureFormat.BC7_SRGB);

        for (int fy = 0; fy < 4; fy++) {
            for (int fx = 0; fx < 4; fx++) {
                var megaFile = mega2Files.get(fy * 4 + fx);
                var mega = megaFile.mega();
                var megaLevel = mega.levels().get(level);
                var lx = fx * megaLevel.blockXCount();
                var ly = fy * megaLevel.blockYCount();

                for (int y = 0; y < megaLevel.blockYCount(); y++) {
                    for (int x = 0; x < megaLevel.blockXCount(); x++) {
                        var source = megaFile.forTile(level, x, y);
                        if (source == null) {
                            continue;
                        }

                        var pageHeader = Mega2PageHeader.read(source);
                        source.skip(pageHeader.diffuseSize() + pageHeader.specularSize() + pageHeader.lightmapSize());
                        if (pageHeader.colorMaskSize() == 0) {
                            continue;
                        }
                        var compressed = source.readBytes(pageHeader.colorMaskSize());
                        var decompressed = Decompressor.lz4Block().decompress(compressed, 16384);
                        var surface = new Surface(TextureFormat.BC7_SRGB, 128, 128, 1, decompressed);

                        Surface.copy(surface, 4, 4, target, (lx + x) * 120, (ly + y) * 120, 120, 120);
                    }
                }
            }
        }

        var decoded = target.toTexture().convert(TextureFormat.R8G8B8A8_UNORM, false);
        var mutable = ((Bytes.Mutable) decoded.getSurface(0, 0).data());
        for (int i = 3; i < mutable.length(); i += 4) {
            mutable.set(i, (byte) 255);
        }

        Exporter<Texture> exporter = Exporter.forType(Texture.class).findFirst().orElseThrow();
        exporter.export(decoded, Path.of("colormask.png"));
    }
}
