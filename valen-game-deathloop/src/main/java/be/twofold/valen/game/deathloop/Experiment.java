package be.twofold.valen.game.deathloop;

import be.twofold.valen.core.texture.*;

import java.io.*;
import java.nio.file.*;

public class Experiment {
    public static void main(String[] args) throws IOException {
        var path = Path.of("D:\\Projects\\Deathloop\\DEATHLOOP\\Deathloop.exe");
        var factory = new DeathloopGameFactory();
        var game = factory.load(path);
        var archive = game.loadArchive("");

        var images = archive.assets().stream()
            .filter(e -> e.id().fileName().endsWith(".bimage"))
            .toList();

        for (var image : images) {
            try {
                archive.loadAsset(image.id(), Texture.class);
            } catch (Exception e) {
                System.out.println("Failed for: " + image.id() + " - " + e.getMessage());
            }
        }
    }
}
