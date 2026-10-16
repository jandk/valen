package be.twofold.valen.game.eternal.reader.soundmetadata;

import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.util.*;

public record Container(
    String name,
    List<ContainerFile> files
) {
    public static Container read(BinarySource source) throws IOException {
        var name = source.readString(StringFormat.INT_LENGTH);
        var files = source.readObjects(source.readInt(), ContainerFile::read);

        return new Container(
            name,
            files
        );
    }
}
