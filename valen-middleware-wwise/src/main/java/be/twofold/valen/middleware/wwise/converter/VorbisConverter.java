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

    private final BinarySource source;
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
        this.source = BinarySource.wrap(audio.data());
        this.wem = Wem.read(source);
        this.vorbisHeader = (AkVorbisHeader) wem.format().codecHeader().orElseThrow();
        this.audioChannels = audio.channels().size();
        this.sampleRate = audio.sampleRate();
    }

    Bytes convert() throws IOException {
        oggWriter.addPacket(rebuildIdentPacket(), 0);
        oggWriter.flushPage();

        source.position(wem.dataOffset() + vorbisHeader.seekTableSize());
        oggWriter.addPacket(rebuildCommentPacket(), 0);
        oggWriter.addPacket(rebuildSetupHeader(), 0);
        oggWriter.flushPage();

        source.position(wem.dataOffset() + vorbisHeader.vorbisDataOffset());
        rebuildAudioPackets();

        return Bytes.wrap(out.toByteArray());
    }

    private void rebuildAudioPackets() throws IOException {
        long end = wem.dataOffset() + wem.dataSize();

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

    private Bytes rebuildAudioPacket(BitReader reader, int modeNumber, boolean blockFlag, boolean nextBlockFlag) {
        var writer = new BitWriter(ByteOrder.LITTLE_ENDIAN);

        writer.write(1, 0);
        writer.write(modeBits, modeNumber);
        if (blockFlag) {
            writer.writeBool(previousBlockFlag);
            writer.writeBool(nextBlockFlag);
        }
        reader.copyTo(writer, reader.bitsRemaining());

        return Bytes.wrap(writer.toByteArray());
    }

    private BitReader readPacket(long end) throws IOException {
        if (source.position() >= end) {
            return null;
        }
        int size = Short.toUnsignedInt(source.readShort());
        return new BitReader(source.readBytes(size).toArray(), ByteOrder.LITTLE_ENDIAN);
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
        int length = Short.toUnsignedInt(source.readShort());
        byte[] packedBytes = source.readBytes(length).toArray();
        BitReader reader = new BitReader(packedBytes, ByteOrder.LITTLE_ENDIAN);

        BitWriter writer = new BitWriter(ByteOrder.LITTLE_ENDIAN);
        writeHeader(writer, 5);

        rebuildCodebooks(reader, writer);
        rebuildTimeDomain(writer);
        rebuildFloors(reader, writer);
        rebuildResidues(reader, writer);
        rebuildMappings(reader, writer);
        var modeBlockFlags = rebuildModes(reader, writer);
        writer.writeBool(true);

        if (reader.isOverrun()) {
            throw new IOException("Setup header overrun");
        }

        this.modeBlockFlags = modeBlockFlags;
        this.modeBits = ilog(modeBlockFlags.length - 1);

        return Bytes.wrap(writer.toByteArray());
    }

    private void rebuildCodebooks(BitReader reader, BitWriter writer) throws IOException {
        int codebookCount = reader.read(8) + 1;
        writer.write(8, codebookCount - 1);

        List<Codebook> library = LIBRARY.get();
        for (int i = 0; i < codebookCount; i++) {
            int codebookId = reader.read(10);
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

    private void rebuildFloors(BitReader reader, BitWriter writer) {
        int floorCount = reader.read(6) + 1;
        writer.write(6, floorCount - 1);

        for (int i = 0; i < floorCount; i++) {
            writer.write(16, 1);
            rebuildFloor1(reader, writer);
        }
    }

    private void rebuildFloor1(BitReader reader, BitWriter writer) {
        int partitions = reader.read(5);
        writer.write(5, partitions);

        int maximumClass = -1;
        int[] partitionClassList = new int[partitions];
        for (int i = 0; i < partitions; i++) {
            int partitionClass = reader.read(4);
            maximumClass = Math.max(maximumClass, partitionClass);
            partitionClassList[i] = partitionClass;

            writer.write(4, partitionClass);
        }

        int[] classDimensions = new int[maximumClass + 1];
        for (int i = 0; i < maximumClass + 1; i++) {
            classDimensions[i] = reader.read(3) + 1;
            int classSubclass = reader.read(2);

            writer.write(3, classDimensions[i] - 1);
            writer.write(2, classSubclass);

            if (classSubclass != 0) {
                int classMasterBook = reader.read(8);

                writer.write(8, classMasterBook);
            }

            for (int j = 0; j < 1 << classSubclass; j++) {
                int subclassBookPlus1 = reader.read(8);

                writer.write(8, subclassBookPlus1);
            }
        }

        int multiplierMinus1 = reader.read(2);
        int rangeBits = reader.read(4);

        writer.write(2, multiplierMinus1);
        writer.write(4, rangeBits);

        int floorValues = 0;
        for (int i = 0; i < partitions; i++) {
            floorValues += classDimensions[partitionClassList[i]];
        }

        reader.copyTo(writer, floorValues * rangeBits);
    }

    private void rebuildResidues(BitReader reader, BitWriter writer) {
        int residueCount = reader.read(6) + 1;
        writer.write(6, residueCount - 1);

        for (int i = 0; i < residueCount; i++) {
            rebuildResidue(reader, writer);
        }
    }

    private void rebuildResidue(BitReader reader, BitWriter writer) {
        int type = reader.read(2);
        int begin = reader.read(24);
        int end = reader.read(24);
        int partitionSize = reader.read(24) + 1;
        int classifications = reader.read(6) + 1;
        int classBook = reader.read(8);

        writer.write(16, type);
        writer.write(24, begin);
        writer.write(24, end);
        writer.write(24, partitionSize - 1);
        writer.write(6, classifications - 1);
        writer.write(8, classBook);

        int[] cascade = new int[classifications];
        for (int i = 0; i < classifications; i++) {
            int highBits = 0;
            int lowBits = reader.read(3);
            boolean bitflag = reader.readBool();
            if (bitflag) {
                highBits = reader.read(5);
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
                    int number = reader.read(8);

                    writer.write(8, number);
                }
            }
        }
    }

    private void rebuildMappings(BitReader reader, BitWriter writer) {
        int mappingCount = reader.read(6) + 1;
        writer.write(6, mappingCount - 1);

        for (int i = 0; i < mappingCount; i++) {
            writer.write(16, 0);
            rebuildMapping(reader, writer);
        }
    }

    private void rebuildMapping(BitReader reader, BitWriter writer) {
        boolean submapsFlag = reader.readBool();
        int submaps = submapsFlag
            ? reader.read(4) + 1
            : 1;

        writer.writeBool(submapsFlag);
        if (submapsFlag) {
            writer.write(4, submaps - 1);
        }

        boolean couplingFlag = reader.readBool();
        writer.writeBool(couplingFlag);
        if (couplingFlag) {
            int couplingSteps = reader.read(8) + 1;
            writer.write(8, couplingSteps - 1);

            int audioChannelLog = ilog(audioChannels - 1);
            for (int i = 0; i < couplingSteps; i++) {
                int mag = reader.read(audioChannelLog);
                int ang = reader.read(audioChannelLog);

                writer.write(audioChannelLog, mag);
                writer.write(audioChannelLog, ang);
            }
        }

        int reserved = reader.read(2);
        writer.write(2, reserved);

        if (submaps > 1) {
            for (int i = 0; i < audioChannels; i++) {
                int submap = reader.read(4);

                writer.write(4, submap);
            }
        }

        for (int i = 0; i < submaps; i++) {
            int timeConfig = reader.read(8);
            int floor = reader.read(8);
            int residue = reader.read(8);

            writer.write(8, timeConfig);
            writer.write(8, floor);
            writer.write(8, residue);
        }
    }

    private boolean[] rebuildModes(BitReader reader, BitWriter writer) {
        int modeCount = reader.read(6) + 1;
        writer.write(6, modeCount - 1);

        boolean[] modeBlockFlags = new boolean[modeCount];
        for (int i = 0; i < modeCount; i++) {
            modeBlockFlags[i] = rebuildMode(reader, writer);
        }
        return modeBlockFlags;
    }

    private boolean rebuildMode(BitReader reader, BitWriter writer) {
        boolean blockFlag = reader.read(1) == 1;
        int mapping = reader.read(8);

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
