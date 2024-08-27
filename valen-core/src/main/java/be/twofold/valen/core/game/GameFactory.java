package be.twofold.valen.core.game;

import java.io.*;
import java.nio.file.*;

public interface GameFactory<T extends Game> {

    T load(Path path) throws IOException;

    boolean canLoad(Path path);
}
