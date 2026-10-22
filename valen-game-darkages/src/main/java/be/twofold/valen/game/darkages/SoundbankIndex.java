package be.twofold.valen.game.darkages;

import be.twofold.valen.core.game.*;
import be.twofold.valen.game.darkages.reader.soundmetadata.*;
import be.twofold.valen.game.idtech.reader.soundcontainer.*;
import be.twofold.valen.middleware.wwise.shared.*;
import be.twofold.valen.middleware.wwise.wem.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

final class SoundbankIndex {
    private static final List<AssetReader<?, ?>> READERS = List.of(
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

                var soundContainer = SoundContainer.read(source, SoundFormat.DARK_AGES);
                for (var j = 0; j < soundContainer.entries().size(); j++) {
                    var entry = soundContainer.entries().get(j);
                    var isEnabled = ((containerFile.mask().get(j / 32) >>> (j % 32)) & 1) != 0;
                    if (isEnabled && entry.sizeOnDisk() != 0) {
                        sounds.putIfAbsent(
                            MediaId.of(entry.mediaId()),
                            new ContainerSound(containerName, filePath, soundContainer, entry)
                        );
                    }
                }
            }
        }

        var names = buildNames(metadata, sounds);
        names.putAll(headerNames(sounds));

        var assets = new ArrayList<Asset>();
        for (var sound : sounds.values()) {
            var mediaId = MediaId.of(sound.entry().mediaId());
            var name = names.getOrDefault(mediaId, mediaId.toString()) + ".wem";
            assets.add(mapSoundEntry(sound, sound.containerName().withoutExtension().resolve(name).toString()));
        }

        return new AssetLoader(Archive.of(assets), new StorageManager(sources, Set.of(), decompressors), READERS);
    }

    private static Asset mapSoundEntry(ContainerSound sound, String name) {
        var entry = sound.entry();
        var assetId = new WwiseAssetId(MediaId.of(entry.mediaId()), name);
        var location = new Location.FileSlice(sound.path(), Integer.toUnsignedLong(entry.offset()), entry.sizeOnDisk());
        return new WwiseAsset(assetId, location);
    }

    private static Map<MediaId, String> buildNames(SoundMetadata metadata, Map<MediaId, ContainerSound> sounds) {
        var names = new HashMap<MediaId, String>();
        for (var event : metadata.events()) {
            for (var nameId : event.mediaIds()) {
                var mediaId = MediaId.of(nameId.id());
                if (sounds.containsKey(mediaId)) {
                    names.putIfAbsent(mediaId, event.name() + "#" + mediaId);
                }
            }
        }
        return names;
    }

    private static Map<MediaId, String> headerNames(Map<MediaId, ContainerSound> sounds) throws IOException {
        var names = new HashMap<MediaId, String>();
        for (var sound : sounds.values()) {
            if (!sound.containerName().baseName().startsWith("MUSIC")) {
                continue;
            }
            var entry = sound.entry();
            var header = sound.soundContainer().headers().slice(entry.headerOffset(), entry.headerSize());
            var mediaId = MediaId.of(entry.mediaId());
            Wem.read(BinarySource.wrap(header)).label()
                .ifPresent(label -> names.put(mediaId, label + "#" + mediaId));
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
