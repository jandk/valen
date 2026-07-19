package be.twofold.valen.game.qc;

import be.twofold.valen.core.game.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class QcGameFactory implements GameFactory<QcGame> {
    @Override
    public Set<String> executableNames() {
        return Set.of("QuakeChampions.exe");
    }

    @Override
    public QcGame load(Path path) throws IOException {
        Path clientPath = path.getParent().getParent().getParent();
        return new QcGame(clientPath);
    }
}
