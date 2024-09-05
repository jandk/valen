package be.twofold.valen.game.neworder;

import be.twofold.valen.core.game.*;
import be.twofold.valen.game.neworder.master.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public final class NewOrderGame implements Game {
    private final Master master;

    public NewOrderGame(Path root) throws IOException {
        var base = root.resolve("base");
        this.master = Master.read(base.resolve("master.index"));
    }

    @Override
    public List<String> archiveNames() {
        return List.of();
    }

    @Override
    public Archive loadArchive(String name) throws IOException {
        return new NewOrderArchive();
    }
}
