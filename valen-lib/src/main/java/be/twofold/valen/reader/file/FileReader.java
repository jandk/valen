package be.twofold.valen.reader.file;

import be.twofold.valen.core.util.*;
import be.twofold.valen.reader.*;
import be.twofold.valen.resource.*;

public final class FileReader implements ResourceReader<File> {
    @Override
    public File read(BetterBuffer buffer, Resource resource) {
        return File.read(buffer);
    }
}
