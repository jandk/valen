package be.twofold.valen.game.neworder.master;

import be.twofold.valen.core.io.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public record Master(
    MasterHeader header,
    List<MasterContainer> containers
) {
    public static Master read(Path path) throws IOException {
        try (var source = DataSource.fromPath(path)) {
            var header = MasterHeader.read(source);
            var containers = source.readStructs(header.count(), MasterContainer::read);
            source.expectEnd();

            return new Master(header, containers);
        }
    }
}
