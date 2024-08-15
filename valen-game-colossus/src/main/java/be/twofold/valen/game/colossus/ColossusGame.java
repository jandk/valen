package be.twofold.valen.game.colossus;

import be.twofold.valen.core.game.*;
import be.twofold.valen.game.colossus.reader.packagemapspec.*;
import be.twofold.valen.game.colossus.resource.*;
import be.twofold.valen.game.colossus.texdb.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class ColossusGame implements Game {
    private final Path base;
    private final PackageMapSpec spec;

    public ColossusGame(Path path) throws IOException {
        this.base = path.resolve("base");
        this.spec = PackageMapSpec.read(base.resolve("packagemapspec.json"));
    }

    @Override
    public List<String> archiveNames() {
        return spec.maps().stream()
            .map(PackageMapSpecMap::name)
            .toList();
    }

    @Override
    public Archive loadArchive(String name) throws IOException {
        List<ResourcesFile> resourcesFiles = new ArrayList<>();
        List<TexDbFile> texDbFiles = new ArrayList<>();
        for (int i = spec.patchLevel(); i > 0; i--) {
            var resourcesPath = base.resolve("patch_" + i + ".resources");
            var texDbPath = base.resolve("patch_" + i + ".texdb");
            resourcesFiles.add(new ResourcesFile(resourcesPath));
            texDbFiles.add(new TexDbFile(texDbPath));
        }
        resourcesFiles.add(new ResourcesFile(base.resolve("gameresources.resources")));
        texDbFiles.add(new TexDbFile(base.resolve("gameresources.texdb")));

        return new ColossusArchive(resourcesFiles, texDbFiles);
    }
}
