package be.twofold.valen.game.colossus;

import be.twofold.valen.core.game.*;
import be.twofold.valen.game.colossus.reader.packagemapspec.*;
import be.twofold.valen.game.colossus.reader.texdb.*;
import be.twofold.valen.game.colossus.resource.*;
import be.twofold.valen.game.colossus.texdb.*;
import wtf.reversed.toolbox.compress.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class ColossusGame implements Game {
    private final Path base;
    private final PackageMapSpec spec;
    private final Decompressor decompressor;

    public ColossusGame(Path path) throws IOException {
        this.base = path.resolve("base");
        this.spec = PackageMapSpec.read(base.resolve("packagemapspec.json"));
        this.decompressor = Decompressor.oodle(path.resolve("oo2core_5_win64.dll"));
    }

    @Override
    public List<String> archiveNames() {
        return spec.maps().stream()
            .map(PackageMapSpecMap::name)
            .toList();
    }

    @Override
    public ColossusArchive loadArchive(String name) throws IOException {
//        for (int i = spec.patchLevel(); i > 0; i--) {
//            var resourcesPath = base.resolve("patch_" + i + ".resources");
//            var texDbPath = base.resolve("patch_" + i + ".texdb");
//            resourcesFiles.add(new ResourcesFile(resourcesPath));
//            texDbFiles.add(new TexDbFile(texDbPath));
//        }
//        resourcesFiles.add(new ResourcesFile(base.resolve("gameresources.resources")));
//        texDbFiles.add(new TexDbFile(base.resolve("gameresources.texdb")));

        List<Container<ColossusAssetID, ColossusAsset>> resourcesFiles = new ArrayList<>();
        resourcesFiles.add(new ResourcesFile(base.resolve("chunk_1.resources"), decompressor));

        List<Container<Long, TexDbEntry>> texDbFiles = new ArrayList<>();
        texDbFiles.add(new TexDbFile(base.resolve("chunk_1.texdb")));

        return new ColossusArchive(
            Container.compose(resourcesFiles),
            Container.compose(texDbFiles),
            decompressor
        );
    }
}
