package be.twofold.valen.game.fear;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.game.fear.reader.ltarchive.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public final class FearArchive implements Archive {
    private final DataSource source;
    private final LTArchive archive;

    public FearArchive(Path path) throws IOException {
        this.source = DataSource.fromPath(path);
        this.archive = LTArchive.read(source);
        assets();
    }

    @Override
    public List<Asset> assets() {
        var directories = new ArrayList<LTArchiveDirectoryEntry>();
        for (var entry : archive.directoryEntries()) {
            for (int i = 0; i < entry.numFiles(); i++) {
                directories.add(entry);
            }
        }

        var base = Path.of("D:\\Projects\\FEAR\\FEAR.Arch00");
        var names = archive.names().names();
        var assets = new ArrayList<Asset>();
        for (int i = 0; i < archive.fileEntries().size(); i++) {
            var entry = archive.fileEntries().get(i);
            var directory = directories.get(i);
            var directoryName = names.get(directory.nameOffset());
            var fileName = names.get(entry.nameOffset());
            var fullName = directoryName + "\\" + fileName;

            var assetID = new FearAssetID(fullName);
            var result = base.resolve(fullName);

//            try {
//                if (Files.exists(result)) {
//                    continue;
//                }
//                Files.createDirectories(result.getParent());
//                source.seek(entry.fileOffset());
//                byte[] bytes = source.readBytes(Math.toIntExact(entry.fileLength1()));
//                Files.write(result, bytes);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }

            var asset = new Asset(assetID, AssetType.BINARY, Math.toIntExact(entry.fileLength1()), Map.of());
            assets.add(asset);
        }
        return assets;
    }

    @Override
    public boolean exists(AssetID identifier) {
        return false;
    }

    @Override
    public <T> T loadAsset(AssetID identifier, Class<T> clazz) throws IOException {
        return null;
    }
}
