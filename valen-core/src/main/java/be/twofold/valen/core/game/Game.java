package be.twofold.valen.core.game;

import java.io.*;
import java.util.*;

public interface Game {

    List<String> archiveNames();

    Archive<?> loadArchive(String name) throws IOException;

}
