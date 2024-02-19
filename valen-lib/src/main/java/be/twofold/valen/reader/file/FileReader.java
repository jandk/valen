package be.twofold.valen.reader.file;

import be.twofold.valen.core.util.*;
import be.twofold.valen.reader.*;
import be.twofold.valen.resource.*;
import jakarta.inject.*;

public final class FileReader implements ResourceReader<File> {

    @Inject
    public FileReader() {
    }

    @Override
    public boolean canRead(Resource entry) {
        return entry.type() == ResourceType.File;
    }

    @Override
    public File read(BetterBuffer buffer, Resource resource) {
        return File.read(buffer);
    }

}
