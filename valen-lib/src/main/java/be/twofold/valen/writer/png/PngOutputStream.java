package be.twofold.valen.writer.png;

import be.twofold.valen.core.util.*;

import java.io.*;
import java.nio.*;
import java.util.zip.*;

/**
 * This might be a dumb idea, because Java already has a {@link javax.imageio.ImageIO} class.
 * But there's a bug in the PNG writer that causes it to use the wrong filter type.
 * <p>
 * So here we are. Good thing it's not that hard to write a PNG file.
 */
final class PngOutputStream implements AutoCloseable {
    private static final byte[] Magic = new byte[]{(byte) 0x89, 0x50, 0x4e, 0x47, 0x0d, 0x0a, 0x1a, 0x0a};
    private static final int IHDR = 0x49484452;
    private static final int PLTE = 0x504c5445;
    private static final int IDAT = 0x49444154;
    private static final int IEND = 0x49454e44;

    private final OutputStream output;
    private final PngFormat format;

    // Filtering
    private final byte[][] filtered;
    private final int[] filterCounts = new int[5];
    private byte[] previous;
    private byte[] current;

    // IDAT
    private final Deflater deflater = new Deflater(Deflater.BEST_SPEED);
    private final byte[] idatBuffer = new byte[32 * 1024];
    private int idatLength = 0;

    PngOutputStream(OutputStream output, PngFormat format) {
        this.output = Check.notNull(output, "output is null");
        this.format = Check.notNull(format, "format is null");
        this.filtered = new byte[5][format.bytesPerPixel() + format.bytesPerRow()];
        this.previous = new byte[format.bytesPerPixel() + format.bytesPerRow()];
        this.current = new byte[format.bytesPerPixel() + format.bytesPerRow()];

        try {
            output.write(Magic);
            writeIHDR();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    void writeImage(byte[] image) throws IOException {
        if (image.length != format.bytesPerImage()) {
            throw new IllegalArgumentException("image has wrong size, expected " + format.bytesPerImage() + " but was " + image.length);
        }
        for (int y = 0; y < format.height(); y++) {
            writeRow(image, y * format.bytesPerRow());
        }
        System.out.printf("Filter counts - None: %d, Sub: %d, Up: %d, Average: %d, Paeth: %d%n",
            filterCounts[0], filterCounts[1], filterCounts[2], filterCounts[3], filterCounts[4]);
    }

    private void writeRow(byte[] image, int offset) throws IOException {
        if (offset + format.bytesPerRow() > image.length) {
            throw new IllegalArgumentException("image has wrong size, expected at least " + (offset + format.bytesPerRow()) + " but was " + image.length);
        }
        int filterMethod = filter(image, offset);
        filterCounts[filterMethod]++;
        deflate(new byte[]{(byte) filterMethod}, 0, 1);
        deflate(filtered[filterMethod], format.bytesPerPixel(), format.bytesPerRow());
    }

    // region Filtering

    private int filter(byte[] row, int offset) {
        int bpp = format.bytesPerPixel();
        int bpr = format.bytesPerRow();

        byte[] nRow = filtered[0];
        byte[] sRow = filtered[1];
        byte[] uRow = filtered[2];
        byte[] aRow = filtered[3];
        byte[] pRow = filtered[4];

        System.arraycopy(row, offset, current, bpp, bpr);
        System.arraycopy(row, offset, nRow, bpp, bpr);
        for (int i = bpp; i < bpp + bpr; i++) {
            int x = Byte.toUnsignedInt(current[i]);
            int a = Byte.toUnsignedInt(current[i - bpp]);
            int b = Byte.toUnsignedInt(previous[i]);
            int c = Byte.toUnsignedInt(previous[i - bpp]);

            sRow[i] = (byte) (x - a);
            uRow[i] = (byte) (x - b);
            aRow[i] = (byte) (x - (a + b >> 1));
            pRow[i] = (byte) (x - paeth(a, b, c));
        }

        int best = findBest();
        byte[] temp = previous;
        previous = current;
        current = temp;
        return best;
    }

    private int findBest() {
        int bestRow = 0;
        int bestSad = Integer.MAX_VALUE;
        for (int i = 0; i < 5; i++) {
            int sad = 0;
            for (byte pixel : filtered[i]) {
                sad += Math.abs(pixel);
            }
            if (sad < bestSad) {
                bestRow = i;
                bestSad = sad;
            }
        }
        return bestRow;
    }

    private static int paeth(int a, int b, int c) {
        int p = a + b - c;
        int pa = Math.abs(p - a);
        int pb = Math.abs(p - b);
        int pc = Math.abs(p - c);

        if (pa <= pb && pa <= pc) {
            return a;
        }
        if (pb <= pc) {
            return b;
        }
        return c;
    }

    // endregion

    // region Chunk writing

    private void writeIHDR() throws IOException {
        byte[] chunk = ByteBuffer.allocate(13)
            .putInt(format.width())
            .putInt(format.height())
            .put((byte) format.bitDepth())
            .put((byte) format.colorType().code())
            .put((byte) 0)
            .put((byte) 0)
            .put((byte) 0)
            .array();
        writeChunk(IHDR, chunk);
    }

    private void writeIDAT() throws IOException {
        writeChunk(IDAT, idatBuffer, idatLength);
        idatLength = 0;
    }

    private void writeIEND() throws IOException {
        writeChunk(IEND, new byte[0]);
    }

    private void writeChunk(int type, byte[] data) throws IOException {
        writeChunk(type, data, data.length);
    }

    private void writeChunk(int type, byte[] data, int length) throws IOException {
        byte[] rawType = toBytes(type);
        CRC32 crc32 = new CRC32();
        crc32.update(rawType);
        crc32.update(data, 0, length);

        output.write(toBytes(length));
        output.write(rawType);
        output.write(data, 0, length);
        output.write(toBytes((int) crc32.getValue()));
    }

    private static byte[] toBytes(int value) {
        return new byte[]{
            (byte) (value >> 24),
            (byte) (value >> 16),
            (byte) (value >> 8),
            (byte) value
        };
    }

    // endregion

    // region Deflate

    private void deflate(byte[] array, int offset, int length) throws IOException {
        deflater.setInput(array, offset, length);
        while (!deflater.needsInput()) {
            deflate();
        }
    }

    private void deflate() throws IOException {
        int len = deflater.deflate(idatBuffer, idatLength, idatBuffer.length - idatLength);
        if (len > 0) {
            idatLength += len;
            if (idatLength == idatBuffer.length) {
                writeIDAT();
            }
        }
    }

    // endregion

    @Override
    public void close() {
        try {
            deflater.finish();
            while (!deflater.finished()) {
                deflate();
            }
            deflater.end();

            writeIDAT();
            writeIEND();
            output.close();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
