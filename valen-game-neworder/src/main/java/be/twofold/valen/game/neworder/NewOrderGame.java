package be.twofold.valen.game.neworder;

import be.twofold.valen.core.game.*;
import be.twofold.valen.game.neworder.master.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public final class NewOrderGame implements Game {
    private final Master master;
    private final Path base;

    public NewOrderGame(Path root) throws IOException {
        this.base = root.resolve("base");
        this.master = Master.read(base.resolve("master.index"));
    }

    @Override
    public List<String> archiveNames() {
        return master.containers().stream()
            .map(MasterContainer::name)
            .toList();
    }

    @Override
    public NewOrderArchive loadArchive(String name) throws IOException {
        var container = master.containers().stream()
            .filter(mc -> mc.name().equals(name))
            .findFirst()
            .orElseThrow(() -> new IOException("Archive not found: " + name));

        return new NewOrderArchive(base, container);
    }
}
