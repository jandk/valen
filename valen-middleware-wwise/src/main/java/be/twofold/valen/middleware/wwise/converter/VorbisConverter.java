package be.twofold.valen.middleware.wwise.converter;

import be.twofold.valen.core.audio.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.middleware.wwise.codebook.*;
import be.twofold.valen.middleware.wwise.converter.vorbis.*;
import be.twofold.valen.middleware.wwise.util.*;
import be.twofold.valen.middleware.wwise.wem.*;
import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.nio.*;
import java.nio.charset.*;
import java.util.*;

final class VorbisConverter {
    private static final byte[] MAGIC = {'v', 'o', 'r', 'b', 'i', 's'};

    private static final ThrowingSupplier<List<Codebook>, IOException> LIBRARY = ThrowingSupplier.lazy(() -> {
        try (InputStream in = VorbisConverter.class.getResourceAsStream("/codebooks.bin")) {
            if (in == null) {
                throw new FileNotFoundException("Could not find codebooks.bin");
            }
            return Codebooks.load(in).library("aoTuV_603");
        }
    });

    private final BinaryReader reader;
    private final Wem wem;
    private final AkVorbisHeader vorbisHeader;
    private final int audioChannels;
    private final int sampleRate;

    private final ByteArrayOutputStream out = new ByteArrayOutputStream();
    private final OggWriter oggWriter = new OggWriter(out);

    private boolean[] modeBlockFlags;
    private int modeBits;
    private boolean previousBlockFlag;

    VorbisConverter(Audio audio) throws IOException {
        this.wem = Wem.read(BinaryReader.wrap(audio.data()));
        this.reader = BinaryReader.wrap(wem.data().payload());
        this.vorbisHeader = (AkVorbisHeader) wem.format().codecHeader().orElseThrow();
        this.audioChannels = audio.channels().size();
        this.sampleRate = audio.sampleRate();
    }

    Bytes convert() throws IOException {
        oggWriter.addPacket(rebuildIdentPacket(), 0);
        oggWriter.flushPage();

        reader.position(vorbisHeader.seekTableSize());
        oggWriter.addPacket(rebuildCommentPacket(), 0);
        oggWriter.addPacket(rebuildSetupHeader(), 0);
        oggWriter.flushPage();

        reader.position(vorbisHeader.vorbisDataOffset());
        rebuildAudioPackets();

        return Bytes.wrap(out.toByteArray());
    }

    private void rebuildAudioPackets() throws IOException {
        long end = wem.data().payload().length();

        int shortBlock = 1 << vorbisHeader.blockSize0();
        int longBlock = 1 << vorbisHeader.blockSize1();

        BitReader current = readPacket(end);
        BitReader next = readPacket(end);
        long granulePosition = 0;
        boolean first = true;

        while (current != null) {
            int modeNumber = current.read(modeBits);
            boolean blockFlag = modeBlockFlags[modeNumber];
            boolean nextBlockFlag = next != null && modeBlockFlags[next.peek(modeBits)];

            if (!first) {
                int previousSize = previousBlockFlag ? longBlock : shortBlock;
                int currentSize = blockFlag ? longBlock : shortBlock;
                granulePosition += previousSize / 4 + currentSize / 4;
            }
            first = false;

            var packet = rebuildAudioPacket(current, modeNumber, blockFlag, nextBlockFlag);
            oggWriter.addPacket(packet, Math.min(granulePosition, vorbisHeader.totalPCMFrames()));
            previousBlockFlag = blockFlag;

            current = next;
            next = readPacket(end);
        }
        oggWriter.finish();
    }

    private Bytes rebuildAudioPacket(BitReader bitReader, int modeNumber, boolean blockFlag, boolean nextBlockFlag) {
        var writer = new BitWriter(ByteOrder.LITTLE_ENDIAN);

        writer.write(1, 0);
        writer.write(modeBits, modeNumber);
        if (blockFlag) {
            writer.writeBool(previousBlockFlag);
            writer.writeBool(nextBlockFlag);
        }
        bitReader.copyTo(writer, bitReader.bitsRemaining());

        return Bytes.wrap(writer.toByteArray());
    }

    private BitReader readPacket(long end) throws IOException {
        if (reader.position() >= end) {
            return null;
        }
        int size = reader.readUnsignedShort();
        return new BitReader(reader.readBytes(size).toArray(), ByteOrder.LITTLE_ENDIAN);
    }

    private Bytes rebuildIdentPacket() {
        BitWriter writer = new BitWriter(ByteOrder.LITTLE_ENDIAN);

        writeHeader(writer, 1);
        writer.write(32, 0); // vorbisVersion
        writer.write(8, audioChannels); // audioChannels
        writer.write(32, sampleRate); // audioSampleRate
        writer.write(32, 0); // bitrateMaximum
        writer.write(32, wem.format().avgBytesPerSec() * 8); // bitrateNominal
        writer.write(32, 0); // bitrateMinimum
        writer.write(8, vorbisHeader.blockSize1() << 4 | vorbisHeader.blockSize0());
        writer.write(8, 1);

        return Bytes.wrap(writer.toByteArray());
    }

    private Bytes rebuildCommentPacket() {
        BitWriter writer = new BitWriter(ByteOrder.LITTLE_ENDIAN);
        writeHeader(writer, 3);

        writeString(writer, Valen.software());
        writer.write(32, 1);
        writeString(writer, "COMMENT=Exported with Valen " + Valen.version());
        writer.writeBool(true);

        return Bytes.wrap(writer.toByteArray());
    }

    private void writeString(BitWriter writer, String s) {
        byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
        writer.write(32, bytes.length);
        for (byte b : bytes) {
            writer.write(8, b);
        }
    }

    private void writeHeader(BitWriter writer, int value) {
        writer.write(8, value);
        for (byte b : MAGIC) {
            writer.write(8, b);
        }
    }

    // region SetupHeader

    private Bytes rebuildSetupHeader() throws IOException {
        int length = reader.readUnsignedShort();
        byte[] packedBytes = reader.readBytes(length).toArray();
        BitReader bitReader = new BitReader(packedBytes, ByteOrder.LITTLE_ENDIAN);

        BitWriter writer = new BitWriter(ByteOrder.LITTLE_ENDIAN);
        writeHeader(writer, 5);

        rebuildCodebooks(bitReader, writer);
        rebuildTimeDomain(writer);
        rebuildFloors(bitReader, writer);
        rebuildResidues(bitReader, writer);
        rebuildMappings(bitReader, writer);
        var modeBlockFlags = rebuildModes(bitReader, writer);
        writer.writeBool(true);

        if (bitReader.isOverrun()) {
            throw new IOException("Setup header overrun");
        }

        this.modeBlockFlags = modeBlockFlags;
        this.modeBits = ilog(modeBlockFlags.length - 1);

        return Bytes.wrap(writer.toByteArray());
    }

    private void rebuildCodebooks(BitReader bitReader, BitWriter writer) throws IOException {
        int codebookCount = bitReader.read(8) + 1;
        writer.write(8, codebookCount - 1);

        List<Codebook> library = LIBRARY.get();
        for (int i = 0; i < codebookCount; i++) {
            int codebookId = bitReader.read(10);
            if (codebookId >= library.size()) {
                throw new IOException("Codebook id out of range: " + codebookId);
            }
            Codebook codebook = library.get(codebookId);

            new BitReader(codebook.bytes().toArray(), ByteOrder.LITTLE_ENDIAN)
                .copyTo(writer, codebook.bitCount());
        }
    }

    private void rebuildTimeDomain(BitWriter writer) {
        writer.write(6, 0);
        writer.write(16, 0);
    }

    private void rebuildFloors(BitReader bitReader, BitWriter writer) {
        int floorCount = bitReader.read(6) + 1;
        writer.write(6, floorCount - 1);

        for (int i = 0; i < floorCount; i++) {
            writer.write(16, 1);
            rebuildFloor1(bitReader, writer);
        }
    }

    private void rebuildFloor1(BitReader bitReader, BitWriter writer) {
        int partitions = bitReader.read(5);
        writer.write(5, partitions);

        int maximumClass = -1;
        int[] partitionClassList = new int[partitions];
        for (int i = 0; i < partitions; i++) {
            int partitionClass = bitReader.read(4);
            maximumClass = Math.max(maximumClass, partitionClass);
            partitionClassList[i] = partitionClass;

            writer.write(4, partitionClass);
        }

        int[] classDimensions = new int[maximumClass + 1];
        for (int i = 0; i < maximumClass + 1; i++) {
            classDimensions[i] = bitReader.read(3) + 1;
            int classSubclass = bitReader.read(2);

            writer.write(3, classDimensions[i] - 1);
            writer.write(2, classSubclass);

            if (classSubclass != 0) {
                int classMasterBook = bitReader.read(8);

                writer.write(8, classMasterBook);
            }

            for (int j = 0; j < 1 << classSubclass; j++) {
                int subclassBookPlus1 = bitReader.read(8);

                writer.write(8, subclassBookPlus1);
            }
        }

        int multiplierMinus1 = bitReader.read(2);
        int rangeBits = bitReader.read(4);

        writer.write(2, multiplierMinus1);
        writer.write(4, rangeBits);

        int floorValues = 0;
        for (int i = 0; i < partitions; i++) {
            floorValues += classDimensions[partitionClassList[i]];
        }

        bitReader.copyTo(writer, floorValues * rangeBits);
    }

    private void rebuildResidues(BitReader bitReader, BitWriter writer) {
        int residueCount = bitReader.read(6) + 1;
        writer.write(6, residueCount - 1);

        for (int i = 0; i < residueCount; i++) {
            rebuildResidue(bitReader, writer);
        }
    }

    private void rebuildResidue(BitReader bitReader, BitWriter writer) {
        int type = bitReader.read(2);
        int begin = bitReader.read(24);
        int end = bitReader.read(24);
        int partitionSize = bitReader.read(24) + 1;
        int classifications = bitReader.read(6) + 1;
        int classBook = bitReader.read(8);

        writer.write(16, type);
        writer.write(24, begin);
        writer.write(24, end);
        writer.write(24, partitionSize - 1);
        writer.write(6, classifications - 1);
        writer.write(8, classBook);

        int[] cascade = new int[classifications];
        for (int i = 0; i < classifications; i++) {
            int highBits = 0;
            int lowBits = bitReader.read(3);
            boolean bitflag = bitReader.readBool();
            if (bitflag) {
                highBits = bitReader.read(5);
            }
            cascade[i] = highBits * 8 + lowBits;

            writer.write(3, lowBits);
            writer.writeBool(bitflag);
            if (bitflag) {
                writer.write(5, highBits);
            }
        }

        for (int i = 0; i < classifications; i++) {
            for (int j = 0; j < 8; j++) {
                if ((cascade[i] & (1 << j)) != 0) {
                    int number = bitReader.read(8);

                    writer.write(8, number);
                }
            }
        }
    }

    private void rebuildMappings(BitReader bitReader, BitWriter writer) {
        int mappingCount = bitReader.read(6) + 1;
        writer.write(6, mappingCount - 1);

        for (int i = 0; i < mappingCount; i++) {
            writer.write(16, 0);
            rebuildMapping(bitReader, writer);
        }
    }

    private void rebuildMapping(BitReader bitReader, BitWriter writer) {
        boolean submapsFlag = bitReader.readBool();
        int submaps = submapsFlag
            ? bitReader.read(4) + 1
            : 1;

        writer.writeBool(submapsFlag);
        if (submapsFlag) {
            writer.write(4, submaps - 1);
        }

        boolean couplingFlag = bitReader.readBool();
        writer.writeBool(couplingFlag);
        if (couplingFlag) {
            int couplingSteps = bitReader.read(8) + 1;
            writer.write(8, couplingSteps - 1);

            int audioChannelLog = ilog(audioChannels - 1);
            for (int i = 0; i < couplingSteps; i++) {
                int mag = bitReader.read(audioChannelLog);
                int ang = bitReader.read(audioChannelLog);

                writer.write(audioChannelLog, mag);
                writer.write(audioChannelLog, ang);
            }
        }

        int reserved = bitReader.read(2);
        writer.write(2, reserved);

        if (submaps > 1) {
            for (int i = 0; i < audioChannels; i++) {
                int submap = bitReader.read(4);

                writer.write(4, submap);
            }
        }

        for (int i = 0; i < submaps; i++) {
            int timeConfig = bitReader.read(8);
            int floor = bitReader.read(8);
            int residue = bitReader.read(8);

            writer.write(8, timeConfig);
            writer.write(8, floor);
            writer.write(8, residue);
        }
    }

    private boolean[] rebuildModes(BitReader bitReader, BitWriter writer) {
        int modeCount = bitReader.read(6) + 1;
        writer.write(6, modeCount - 1);

        boolean[] modeBlockFlags = new boolean[modeCount];
        for (int i = 0; i < modeCount; i++) {
            modeBlockFlags[i] = rebuildMode(bitReader, writer);
        }
        return modeBlockFlags;
    }

    private boolean rebuildMode(BitReader bitReader, BitWriter writer) {
        boolean blockFlag = bitReader.read(1) == 1;
        int mapping = bitReader.read(8);

        writer.writeBool(blockFlag);
        writer.write(16, 0);
        writer.write(16, 0);
        writer.write(8, mapping);

        return blockFlag;
    }

    private int ilog(int value) {
        return 32 - Integer.numberOfLeadingZeros(value);
    }

    // endregion
}
