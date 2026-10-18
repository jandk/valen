package be.twofold.valen.middleware.wwise.converter.vorbis;

import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.hash.*;
import wtf.reversed.toolbox.util.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

final class OggWriter {
    private static final CRCAlgorithm CRC_ALGORITHM = new CRCAlgorithm(32, 0x04C11DB7, 0, false, false, 0);
    private static final HashFunction CRC = HashFunction.crc(CRC_ALGORITHM);

    private static final int OggS = 'O' | 'g' << 8 | 'g' << 16 | 'S' << 24;

    private final OutputStream out;
    private long granulePosition;
    private final int bitStreamSerialNumber = ThreadLocalRandom.current().nextInt();
    private int pageSequenceNumber = 0;

    private final List<Bytes> packets = new ArrayList<>();
    private int pendingBytes = 0;
    private int pendingSegments = 0;

    OggWriter(OutputStream out) {
        this.out = Check.nonNull(out, "out");
    }

    public void addPacket(Bytes packet, long granulePosition) throws IOException {
        int segmentCount = packet.length() / 255 + 1;
        if (segmentCount > 255) {
            throw new IOException("Packet too large");
        }
        if (!packets.isEmpty() && (pendingSegments + segmentCount > 255 || pendingBytes + packet.length() > 4096)) {
            flushPage();
        }

        packets.add(packet);
        pendingBytes += packet.length();
        pendingSegments += segmentCount;
        this.granulePosition = granulePosition;
    }

    public void flushPage() throws IOException {
        out.write(buildPage(false));
    }

    public void finish() throws IOException {
        out.write(buildPage(true));
    }

    private byte[] buildSegmentTable() {
        byte[] segmentTable = new byte[pendingSegments];
        for (int i = 0, o = 0; i < packets.size(); i++) {
            int length = packets.get(i).length();
            int segments = length / 255 + 1;
            for (int j = 0; j < segments; j++) {
                segmentTable[o++] = (byte) Math.min(255, length - j * 255);
            }
        }

        return segmentTable;
    }

    private byte[] buildPage(boolean last) {
        byte[] segmentTable = buildSegmentTable();
        int totalPageSize = 27 + segmentTable.length + pendingBytes;

        int bos = pageSequenceNumber == 0 ? 2 : 0;
        int eos = last ? 4 : 0;

        Bytes.Mutable pageData = Bytes.allocate(totalPageSize)
            .setInt(0, OggS)
            .set(5, (byte) (bos | eos))
            .setLong(6, granulePosition)
            .setInt(14, bitStreamSerialNumber)
            .setInt(18, pageSequenceNumber++)
            .set(26, (byte) segmentTable.length);

        Bytes.wrap(segmentTable).copyTo(pageData, 27);

        int offset = 27 + segmentTable.length;
        for (Bytes packet : packets) {
            packet.copyTo(pageData, offset);
            offset += packet.length();
        }

        int hash = CRC.hash(pageData).asInt();
        pageData.setInt(22, hash);

        packets.clear();
        pendingBytes = 0;
        pendingSegments = 0;
        return pageData.toArray();
    }
}
