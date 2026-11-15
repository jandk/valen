package be.twofold.valen.game.source.readers.vtf;

import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.util.*;

public record Vtf(
    VtfHeader header,
    List<VtfResourceEntry> resources
) {
    public static Vtf read(BinarySource source) throws IOException {
        var header = VtfHeader.read(source);
        var resources = source.readObjects(header.numResources(), VtfResourceEntry::read);

        return new Vtf(
            header,
            resources
        );
    }
}
