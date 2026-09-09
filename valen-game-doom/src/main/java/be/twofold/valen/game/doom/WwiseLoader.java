package be.twofold.valen.game.doom;

import be.twofold.valen.core.game.*;
import be.twofold.valen.middleware.wwise.bnk.*;
import be.twofold.valen.middleware.wwise.info.*;
import be.twofold.valen.middleware.wwise.pck.*;
import be.twofold.valen.middleware.wwise.shared.*;
import be.twofold.valen.middleware.wwise.wem.*;
import wtf.reversed.toolbox.io.*;
import wtf.reversed.toolbox.util.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

final class WwiseLoader {
    private final Path root;
    private final Map<Path, BinarySource> sources = new LinkedHashMap<>();
    private final Map<MediaId, WwiseAsset> assets = new LinkedHashMap<>();
    private Map<MediaId, MediaFile> mediaFiles;

    private WwiseLoader(Path root) {
        this.root = Check.nonNull(root, "root");
    }

    public static AssetLoader load(Path root) throws IOException {
        return new WwiseLoader(root).load();
    }

    private AssetLoader load() throws IOException {
        var soundBanksInfo = InfoXmlReader.load(root.resolve("soundbanksinfo.xml"));
        this.mediaFiles = Stream.concat(
                soundBanksInfo.streamedFiles().stream(),
                soundBanksInfo.soundBanks().stream().flatMap(b -> b.includedMemoryFiles().stream()))
            .collect(Collectors.toMap(MediaFile::id, Function.identity(), (a, _) -> a));

        loadPackages(); // Packages go first, they contain whole files
        loadBanks(soundBanksInfo.soundBanks());

        var storageManager = new StorageManager(sources, Set.of(), new Decompressors(null));

        return new AssetLoader(
            Archive.of(List.copyOf(assets.values())),
            storageManager,
            List.of(new WemReader())
        );
    }

    private void loadBanks(List<SoundBank> soundBanks) throws IOException {
        for (var soundBank : soundBanks) {
            var resolved = root.resolve(soundBank.path());
            var source = BinarySource.open(resolved);
            sources.put(resolved, source);

            var bank = Bank.read(source);
            loadBank(bank, resolved);
        }
    }

    private void loadBank(Bank bank, Path resolved) {
        for (var header : bank.index().headers()) {
            var name = nameOf(header.id());
            var location = new Location.FileSlice(resolved, bank.offsetOf(header), header.size());
            assets.put(header.id(), new WwiseAsset(new WwiseAssetId(header.id(), name), location));
        }
    }

    private void loadPackages() throws IOException {
        List<Path> packFilePaths;
        try (var stream = Files.walk(root, 2)) {
            packFilePaths = stream
                .filter(p -> p.getFileName().toString().endsWith(".pck"))
                .toList();
        }

        for (var packFilePath : packFilePaths) {
            var resolved = root.resolve(packFilePath);
            var source = BinarySource.open(resolved);
            sources.put(resolved, source);

            var pack = FilePackage.read(source);
            loadPackage(pack, resolved);
        }
    }

    private void loadPackage(FilePackage pack, Path resolved) {
        for (var entry : pack.streams()) {
            var name = nameOf(entry.fileId());
            var location = new Location.FileSlice(resolved, entry.offset(), entry.fileSize());
            assets.putIfAbsent(entry.fileId(), new WwiseAsset(new WwiseAssetId(entry.fileId(), name), location));
        }
    }

    private String nameOf(MediaId id) {
        var file = mediaFiles.get(id);
        return file != null ? file.path().replace('\\', '/') : null;
    }
}
