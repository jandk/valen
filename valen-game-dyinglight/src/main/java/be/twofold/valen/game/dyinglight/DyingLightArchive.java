package be.twofold.valen.game.dyinglight;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.core.util.collect.*;
import be.twofold.valen.game.dyinglight.reader.mesh.*;
import be.twofold.valen.game.dyinglight.reader.rpack.*;
import be.twofold.valen.game.dyinglight.reader.texture.*;

import java.io.*;
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

    private final AssetReaders<DyingLightAsset> readers = new AssetReaders<>(List.of(
        new DLMeshReader(),
        new DLTextureReader()
    ));

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

        Bytes bytes = readFile(asset.file());
        try (var source = BinaryReader.fromBytes(bytes)) {
            return readers.read(asset, source, clazz);
        }
        // throw new IOException("No reader found for " + asset);
    }

    private Bytes readFile(RDPFile file) throws IOException {
        var parts = this.parts.subList(file.partIndex(), file.partIndex() + file.numParts());
        var size = parts.stream().mapToInt(RDPPart::size).sum();
        var bytes = MutableBytes.allocate(size);
        var offset = 0;
        for (RDPPart part : parts) {
            readPart(part).copyTo(bytes, offset);
            offset += part.size();
        }

        return bytes;
    }

    private Bytes readPart(RDPPart part) throws IOException {
        var section = sections.get(part.sectionIndex());
        var offset = (long) section.offset() + (long) part.offset() << 4;
        return reader
            .position(offset)
            .readBytesStruct(part.size());
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
        String name = filenames.get(file.fileIndex());
        var id = new DyingLightAssetID(name, file.type());
        var parts = this.parts.subList(file.partIndex(), file.partIndex() + file.numParts());
        List<ResourceType> sectionTypes = parts.stream()
            .map(part -> sections.get(part.sectionIndex()).type())
            .toList();
        return new DyingLightAsset(id, file, parts, sectionTypes);
    }
}
