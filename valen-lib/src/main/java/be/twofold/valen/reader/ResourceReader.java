package be.twofold.valen.reader;

import be.twofold.valen.core.io.*;
import be.twofold.valen.resource.*;

import java.io.*;

public interface ResourceReader<R> {

    boolean canRead(Resource entry);

    R read(DataSource source, Resource resource) throws IOException;

}
