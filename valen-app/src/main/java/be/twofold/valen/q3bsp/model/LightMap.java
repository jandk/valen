package be.twofold.valen.q3bsp.model;

import be.twofold.valen.core.io.*;

import java.awt.image.*;
import java.io.*;

public record LightMap(
    byte[] data
) {
    public static final int BYTES = 128 * 128 * 3;

    public static LightMap read(DataSource source) throws IOException {
        return new LightMap(source.readBytes(BYTES));
    }

    public BufferedImage asImage() {
        BufferedImage image = new BufferedImage(128, 128, BufferedImage.TYPE_3BYTE_BGR);
        image.getRaster().setDataElements(0, 0, 128, 128, data);
        return image;
    }
}
