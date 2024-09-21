package be.twofold.valen.game.deathloop;

import java.io.*;
import java.nio.file.*;

public class Experiment {
    public static void main(String[] args) throws IOException {
        var path = Path.of("D:\\Projects\\Deathloop\\DEATHLOOP\\Deathloop.exe");
        var factory = new DeathloopGameFactory();
        var game = factory.load(path);
        var archive = game.loadArchive("");
    }
}
