package be.twofold.valen.game.eternal;

import be.twofold.valen.core.game.*;
import be.twofold.valen.game.eternal.reader.soundcontainer.*;
import be.twofold.valen.game.eternal.reader.soundmetadata.*;
import be.twofold.valen.middleware.wwise.shared.*;
import be.twofold.valen.middleware.wwise.wem.*;
import com.google.gson.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

final class SoundbankIndex {
    private static final List<AssetReader<?, ?>> READERS = List.of(
        new SoundReader(),
        new WemReader()
    );

    private SoundbankIndex() {
    }

    public static AssetLoader build(Path path, Decompressors decompressors) throws IOException {
        var metadata = SoundMetadata.load(path.resolve("soundmetadata.bin"));

        var sounds = new LinkedHashMap<MediaId, ContainerSound>();
        var sources = new HashMap<Path, BinarySource>();

        for (var container : metadata.containers()) {
            var containerName = AssetPath.of(container.name());

            for (var i = 0; i < container.files().size(); i++) {
                var containerFile = container.files().get(i);
                var fileName = i == 0
                    ? containerName
                    : containerName.withSuffix("_patch_" + i);

                var filePath = fileName.toPath(path);
                var source = BinarySource.open(filePath);
                sources.put(filePath, source);

                var soundContainer = SoundContainer.read(source);
                for (var j = 0; j < soundContainer.entries().size(); j++) {
                    var entry = soundContainer.entries().get(j);
                    var isEnabled = ((containerFile.mask().get(j / 32) >>> (j % 32)) & 1) != 0;
                    if (isEnabled && entry.sizeOnDisk() != 0) {
                        sounds.putIfAbsent(entry.mediaId(), new ContainerSound(containerName, filePath, soundContainer, entry));
                    }
                }
            }
        }

        var names = buildNames(metadata, sounds);
        names.putAll(loadMediaNames()); // Just override

        var assets = new ArrayList<Asset>();
        for (var sound : sounds.values()) {
            var mediaId = sound.entry().mediaId();
            var name = names.getOrDefault(mediaId, mediaId.toString() + ".wem");
            assets.add(mapSoundEntry(sound, sound.containerName().withoutExtension().resolve(name).toString()));
        }

        return new AssetLoader(Archive.of(assets), new StorageManager(sources, Set.of(), decompressors), READERS);
    }

    @SuppressWarnings("DataFlowIssue")
    private static Map<MediaId, String> loadMediaNames() throws IOException {
        try (var reader = new InputStreamReader(SoundbankIndex.class.getResourceAsStream("/MediaNames.json"))) {
            var json = JsonParser.parseReader(reader).getAsJsonObject();
            return json.entrySet().stream().collect(Collectors.toUnmodifiableMap(
                e -> MediaId.parse(e.getKey()),
                e -> e.getValue().getAsString()
            ));
        }
    }

    private static Asset mapSoundEntry(ContainerSound sound, String name) {
        var entry = sound.entry();
        var assetId = new WwiseAssetId(entry.mediaId(), name);
        var location = new Location.FileSlice(sound.path(), entry.offset(), entry.sizeOnDisk());
        return switch (entry.type()) {
            case WEM -> new WwiseAsset(assetId, location);
            case OPUS -> {
                var header = sound.soundContainer().headers().slice(entry.headerOffset(), entry.headerSize());
                yield new SoundAsset(assetId, location, header);
            }
        };
    }

    private static Map<MediaId, String> buildNames(SoundMetadata metadata, Map<MediaId, ContainerSound> sounds) {
        var names = new HashMap<MediaId, String>();
        for (var event : metadata.events()) {
            var idsByContainer = new LinkedHashMap<AssetPath, List<MediaId>>();
            for (var i = 0; i < event.mediaIds().length(); i++) {
                var mediaId = MediaId.of(event.mediaIds().get(i));
                var sound = sounds.get(mediaId);
                if (sound == null) {
                    continue;
                }
                var mediaIds = idsByContainer.computeIfAbsent(sound.containerName(), _ -> new ArrayList<>());
                if (!mediaIds.contains(mediaId)) {
                    mediaIds.add(mediaId);
                }
            }

            for (var mediaIds : idsByContainer.values()) {
                for (var i = 0; i < mediaIds.size(); i++) {
                    var name = mediaIds.size() == 1 ? event.name() : event.name() + "_" + i;
                    names.putIfAbsent(mediaIds.get(i), name);
                }
            }
        }
        return names;
    }

    private record ContainerSound(
        AssetPath containerName,
        Path path,
        SoundContainer soundContainer,
        SoundEntry entry
    ) {
    }
}
