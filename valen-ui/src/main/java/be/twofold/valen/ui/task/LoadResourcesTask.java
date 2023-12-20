package be.twofold.valen.ui.task;

import be.twofold.valen.manager.*;
import be.twofold.valen.resource.*;
import javafx.concurrent.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class LoadResourcesTask extends Task<Collection<Resource>> {
    private final Path resourcesPath;

    public LoadResourcesTask(Path resourcesPath) {
        this.resourcesPath = resourcesPath;
    }

    @Override
    protected Collection<Resource> call() {
        Path base = Path.of("D:\\Games\\Steam\\steamapps\\common\\DOOMEternal\\base");

        try {
            FileManager manager = new FileManager(base);
            manager.select("game/tutorials/tutorial_sp");
            return manager.getEntries();
        } catch (IOException e) {
            System.err.println("Failed to open file: " + base);
            throw new UncheckedIOException(e);
        }
    }
}
