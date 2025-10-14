package be.twofold.valen.game.dyinglight;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.core.util.collect.*;
import be.twofold.valen.game.dyinglight.reader.rpack.*;

import java.io.*;
import java.nio.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class DyingLightArchive implements Archive<DyingLightAssetID, DyingLightAsset> {
    private final BinaryReader reader;
    private final RDPHeader header;
    private final List<RDPSection> sections;
    private final List<RDPPart> parts;
    private final List<RDPFile> files;
    private final List<String> filenames;

    private final Map<DyingLightAssetID, DyingLightAsset> index;

    public DyingLightArchive(Path path) throws IOException {
        reader = BinaryReader.fromPath(path);
        header = RDPHeader.read(reader);
        sections = reader.readObjects(header.numSections(), RDPSection::read);
        parts = reader.readObjects(header.numParts(), RDPPart::read);
        files = reader.readObjects(header.numFiles(), RDPFile::read);

        var filenameOffsets = reader.readInts(header.numFilenameOffsets());
        var filenameBytes = reader.readBytes(header.numFilenameBytes());
        filenames = mapFilenames(filenameOffsets, filenameBytes);

        var files = mapFiles();
        this.index = files.stream()
            .filter(asset -> asset.size() > 0)
            .collect(Collectors.toUnmodifiableMap(
                DyingLightAsset::id,
                Function.identity()
            ));
    }

    @Override
    public <T> T loadAsset(DyingLightAssetID identifier, Class<T> clazz) throws IOException {
        var asset = get(identifier).orElseThrow(FileNotFoundException::new);

        if (clazz == ByteBuffer.class) {
            return clazz.cast(readFile(asset.file()));
        }

        throw new IOException("No reader found for " + asset);
    }

    private ByteBuffer readFile(RDPFile file) throws IOException {
        var parts = this.parts.subList(file.partIndex(), file.partIndex() + file.numParts());
        var size = parts.stream().mapToInt(RDPPart::size).sum();
        var buffer = ByteBuffer.allocate(size);

        for (RDPPart part : parts) {
            buffer.put(readPart(part));
        }

        return buffer.flip();
    }

    private ByteBuffer readPart(RDPPart part) throws IOException {
        var section = sections.get(part.sectionIndex());
        var offset = section.offset() + part.offset() << 4;
        return reader
            .position(offset)
            .readBuffer(part.size());
    }

    @Override
    public Optional<DyingLightAsset> get(DyingLightAssetID key) {
        return Optional.ofNullable(index.get(key));
    }

    @Override
    public Stream<DyingLightAsset> getAll() {
        return index.values().stream();
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }

    private static List<String> mapFilenames(int[] filenameOffsets, byte[] filenameBytes) {
        return IntStream.of(filenameOffsets)
            .mapToObj(offset -> {
                var bytes = Bytes.wrap(filenameBytes);
                var length = bytes.slice(offset).indexOf((byte) 0);
                if (length < 0) {
                    length = bytes.size();
                }
                return bytes.slice(offset, offset + length).toString(StandardCharsets.ISO_8859_1).trim();
            })
            .toList();
    }

    private List<DyingLightAsset> mapFiles() {
        return files.stream()
            .map(this::mapFile)
            .toList();
    }

    private DyingLightAsset mapFile(RDPFile file) {
        var id = new DyingLightAssetID(filenames.get(file.fileIndex()));
        var parts = this.parts.subList(file.partIndex(), file.partIndex() + file.numParts());
        return new DyingLightAsset(id, file, parts);
    }
}
