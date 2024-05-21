package be.twofold.valen.core.texture.decoder;

import be.twofold.valen.core.texture.*;
import be.twofold.valen.core.texture.writer.dds.*;

import java.io.*;
import java.nio.*;
import java.nio.file.*;
import java.util.*;

public final class BcGenerator {
    private BcGenerator() {
    }

    public static void main(String[] args) {
        genRedBc1();
        genGreenBc1();
        genBlueBc1();
    }

    private static void gen(int count, int shift) {
        ByteBuffer buffer = ByteBuffer
            .allocate(count * count * 8)
            .order(ByteOrder.LITTLE_ENDIAN);

        for (int i = 0; i < count; i++) {
            for (int j = 0; j < count; j++) {
                short c0 = (short) (i << shift);
                short c1 = (short) (j << shift);
                buffer.putShort(c0);
                buffer.putShort(c1);
                buffer.putInt(0xffaa5500);
            }
        }
    }

    private static void genRedBc1() {
        ByteBuffer buffer = ByteBuffer
            .allocate(32 * 32 * 8)
            .order(ByteOrder.LITTLE_ENDIAN);

        for (int r0 = 0; r0 < 32; r0++) {
            for (int r1 = 0; r1 < 32; r1++) {
                short c0 = (short) (r0 << 11);
                short c1 = (short) (r1 << 11);
                buffer.putShort(c0);
                buffer.putShort(c1);
                buffer.putInt(0xffaa5500);
            }
        }

        var surface = new Surface(128, 128, buffer.array());
        var texture = new Texture(128, 128, TextureFormat.Bc1UNorm, List.of(surface), false);
        try (var channel = Files.newByteChannel(Paths.get("red.dds"), StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
            new DdsWriter(channel).write(texture);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void genGreenBc1() {
        ByteBuffer buffer = ByteBuffer
            .allocate(64 * 64 * 8)
            .order(ByteOrder.LITTLE_ENDIAN);

        for (int g0 = 0; g0 < 64; g0++) {
            for (int g1 = 0; g1 < 64; g1++) {
                short c0 = (short) (g0 << 5);
                short c1 = (short) (g1 << 5);
                buffer.putShort(c0);
                buffer.putShort(c1);
                buffer.putInt(0xffaa5500);
            }
        }

        var surface = new Surface(256, 256, buffer.array());
        var texture = new Texture(256, 256, TextureFormat.Bc1UNorm, List.of(surface), false);
        try (var channel = Files.newByteChannel(Paths.get("green.dds"), StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
            new DdsWriter(channel).write(texture);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void genBlueBc1() {
        ByteBuffer buffer = ByteBuffer
            .allocate(32 * 32 * 8)
            .order(ByteOrder.LITTLE_ENDIAN);

        for (int b0 = 0; b0 < 32; b0++) {
            for (int b1 = 0; b1 < 32; b1++) {
                short c0 = (short) (b0 << 0);
                short c1 = (short) (b1 << 0);
                buffer.putShort(c0);
                buffer.putShort(c1);
                buffer.putInt(0xffaa5500);
            }
        }

        var surface = new Surface(128, 128, buffer.array());
        var texture = new Texture(128, 128, TextureFormat.Bc1UNorm, List.of(surface), false);
        try (var channel = Files.newByteChannel(Paths.get("blue.dds"), StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
            new DdsWriter(channel).write(texture);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
