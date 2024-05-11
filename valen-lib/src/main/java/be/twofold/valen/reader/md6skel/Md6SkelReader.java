package be.twofold.valen.reader.md6skel;

import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.reader.*;
import be.twofold.valen.resource.*;
import jakarta.inject.*;

import java.io.*;

public final class Md6SkelReader implements ResourceReader<Skeleton> {
    @Inject
    public Md6SkelReader() {
    }

    @Override
    public boolean canRead(Resource entry) {
        return entry.type() == ResourceType.Skeleton;
    }

    @Override
    public Skeleton read(DataSource source, Resource resource) throws IOException {
        Md6Skel skeleton = Md6Skel.read(source);
        return Md6SkelMapper.map(skeleton);
    }
}
