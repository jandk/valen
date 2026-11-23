package be.twofold.valen.game.idtech.reader.soundmetadata;

import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.util.*;

public record Container(
    String name,
    List<ContainerFile> files
) {
    public static Container read(BinaryReader reader) throws IOException {
        var name = reader.readString(StringFormat.INT_LENGTH);
        var files = reader.readObjects(reader.readInt(), ContainerFile::read);

        return new Container(
            name,
            files
        );
    }
}
