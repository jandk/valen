package be.twofold.valen.core.game;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public interface GameFactory<T extends Game> {

    Set<String> executableNames();

    T load(Path path) throws IOException;

}
