import be.twofold.valen.middleware.wwise.util.*;
import wtf.reversed.toolbox.collect.*;

import java.io.*;
import java.nio.*;
import java.nio.file.*;
import java.util.*;

final class CodebookMerger {
    private static final List<Map.Entry<String, String>> CODEBOOKS = List.of(
        Map.entry("default", "packed_codebooks.bin"),
        Map.entry("aoTuV_603", "packed_codebooks_aoTuV_603.bin")
    );

    static void main() throws IOException {
        Map<Bytes, Integer> codebooks = new LinkedHashMap<>();
        Map<String, int[]> libraries = new LinkedHashMap<>();
        for (Map.Entry<String, String> codebook : CODEBOOKS) {
            List<Bytes> packedLibrary = readPackedLibrary(codebook.getValue());
            int[] indices = new int[packedLibrary.size()];
            for (int i = 0; i < packedLibrary.size(); i++) {
                Bytes packedCodebook = packedLibrary.get(i);
                codebooks.putIfAbsent(packedCodebook, codebooks.size());
                indices[i] = codebooks.get(packedCodebook);
            }

            libraries.put(codebook.getKey(), indices);
        }

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(bytes);
        out.writeBytes("CDBK");
        out.writeShort(1 << 8 | 0);
        out.writeShort(libraries.size());
        out.writeShort(codebooks.size());
        for (Map.Entry<String, int[]> library : libraries.entrySet()) {
            out.writeUTF(library.getKey());
            out.writeShort(library.getValue().length);
            for (int index : library.getValue()) {
                out.writeShort(index);
            }
        }
        for (Bytes packedCodebook : codebooks.keySet()) {
            Bits repacked = repack(packedCodebook);
            out.writeShort(repacked.bitCount());
            out.write(repacked.bytes());
        }
        out.flush();

        Files.write(Path.of("valen-middleware-wwise/src/main/resources/codebooks.bin"), bytes.toByteArray());
    }

    private static Bits repack(Bytes packedCodebook) throws IOException {
        return repackCodebook(packedCodebook);
    }

    private static List<Bytes> readPackedLibrary(String name) throws IOException {
        byte[] bytes;
        try (InputStream in = CodebookMerger.class.getResourceAsStream("/" + name)) {
            bytes = in.readAllBytes();
        }

        ByteBuffer buffer = ByteBuffer.wrap(bytes)
            .order(ByteOrder.LITTLE_ENDIAN);

        int start = buffer.getInt(buffer.limit() - 4);
        int count = (buffer.limit() - start - 4) / 4;

        int[] offsets = new int[count + 1];
        buffer.duplicate().position(start).slice()
            .order(ByteOrder.LITTLE_ENDIAN)
            .asIntBuffer().get(offsets);

        List<Bytes> codebooks = new ArrayList<>();
        for (int i = 0; i < offsets.length - 1; i++) {
            int lo = offsets[i];
            int hi = offsets[i + 1];

            byte[] codebook = new byte[hi - lo];
            buffer.position(lo).get(codebook);
            codebooks.add(Bytes.wrap(codebook));
        }
        return codebooks;
    }

    private static Bits repackCodebook(Bytes bytes) throws IOException {
        BitReader reader = new BitReader(bytes.toArray(), ByteOrder.LITTLE_ENDIAN);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        BitWriter writer = new BitWriter(ByteOrder.LITTLE_ENDIAN);

        int dimensions = reader.read(4);
        int entries = reader.read(14);
        boolean ordered = reader.readBool();

        writer.write(24, 0x564342);
        writer.write(16, dimensions);
        writer.write(24, entries);
        writer.writeBool(ordered);

        if (ordered) {
            int length = reader.read(5) + 1;
            writer.write(5, length - 1);

            int entry = 0;
            while (entry < entries) {
                int bits = ilog(entries - entry);
                int number = reader.read(bits);

                writer.write(bits, number);
                entry += number;
            }
        } else {
            int codewordLengthLength = reader.read(3);
            boolean sparse = reader.readBool();

            writer.writeBool(sparse);

            for (int i = 0; i < entries; i++) {
                boolean present = true;
                if (sparse) {
                    present = reader.readBool();
                    writer.writeBool(present);
                }
                if (present) {
                    int codewordLength = reader.read(codewordLengthLength) + 1;
                    writer.write(5, codewordLength - 1);
                }
            }
        }

        int lookupType = reader.read(1);
        writer.write(4, lookupType);

        if (lookupType == 1) {
            int minimumValue = reader.read(32);
            int deltaValue = reader.read(32);
            int valueBits = reader.read(4) + 1;
            boolean sequenceP = reader.readBool();

            writer.write(32, minimumValue);
            writer.write(32, deltaValue);
            writer.write(4, valueBits - 1);
            writer.writeBool(sequenceP);

            int lookupValues = lookup1Values(entries, dimensions);
            for (int i = 0; i < lookupValues; i++) {
                int multiplicand = reader.read(valueBits);
                writer.write(valueBits, multiplicand);
            }
        }

        // Weird, end on a byte, write another one...
        int used = Math.toIntExact(reader.bitsRead() / 8 + 1);
        if (used != bytes.length()) {
            throw new IOException("Packed codebook used " + used + " of " + bytes.length() + " bytes");
        }

        return new Bits(
            out.toByteArray(),
            writer.bitsWritten()
        );
    }

    private static int lookup1Values(int entries, int dimensions) throws IOException {
        if (entries < 1 || dimensions < 1) {
            throw new IOException("Invalid codebook: entries=" + entries + ", dimensions=" + dimensions);
        }

        int lo = 1;
        int hi = entries;
        while (lo < hi) {
            int mid = lo + (hi - lo + 1) / 2;
            if (powAtMost(mid, dimensions, entries)) {
                lo = mid;
            } else {
                hi = mid - 1;
            }
        }
        return lo;
    }

    private static boolean powAtMost(int base, int exponent, int limit) {
        long acc = 1;
        for (int i = 0; i < exponent; i++) {
            acc *= base;
            if (acc > limit) {
                return false;
            }
        }
        return true;
    }

    private static int ilog(int value) {
        return 32 - Integer.numberOfLeadingZeros(value);
    }

    private record Bits(
        byte[] bytes,
        int bitCount
    ) {
    }
}
