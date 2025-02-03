package be.twofold.valen.game.eternal.reader.file;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.game.eternal.resource.*;

import java.io.*;

public final class FileReader implements AssetReader<File, Resource> {
    @Override
    public boolean canRead(Resource resource) {
        return resource.key().type() == ResourceType.File;
    }

    @Override
    public File read(DataSource source, Resource resource) throws IOException {
        return File.read(source);
    }
}
