package be.twofold.valen.core.game;

import java.io.*;
import java.nio.file.*;

public interface GameFactory<T extends Game> {

    String executableName();

    T load(Path path) throws IOException;

}
