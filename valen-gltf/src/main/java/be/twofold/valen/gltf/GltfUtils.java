package be.twofold.valen.gltf;

import java.io.*;

final class GltfUtils {
    private GltfUtils() {
    }

    public static int alignedLength(int length) {
        return (length + 3) & ~3;
    }

    public static void align(OutputStream output, int length, byte pad) throws IOException {
        int padLength = GltfUtils.alignedLength(length) - length;
        for (int i = 0; i < padLength; i++) {
            output.write(pad);
        }
    }
}
