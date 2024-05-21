package be.twofold.valen.reader.file;

import be.twofold.valen.core.io.*;
import be.twofold.valen.reader.*;
import be.twofold.valen.resource.*;
import jakarta.inject.*;

import java.io.*;

public final class FileReader implements ResourceReader<File> {

    @Inject
    FileReader() {
    }

    @Override
    public boolean canRead(Resource entry) {
        return entry.type() == ResourceType.File;
    }

    @Override
    public File read(DataSource source, Resource resource) throws IOException {
        return File.read(source);
    }

}
