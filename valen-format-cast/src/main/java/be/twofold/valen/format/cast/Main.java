package be.twofold.valen.format.cast;

import be.twofold.valen.format.cast.node.*;

import java.io.*;
import java.nio.file.*;

public class Main {
    public static void main(String[] args) throws IOException {
        try (var in = Files.newInputStream(Path.of("D:\\Software\\Henri-v1.1.8\\exported_files\\models\\hairs\\indiana_wear_head.cast"))) {
            var cast = Cast.read(in);
            CastNode rootNode = cast.getFirst();
            System.out.println(rootNode);
        }
    }
}
