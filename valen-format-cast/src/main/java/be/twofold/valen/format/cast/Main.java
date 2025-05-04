package be.twofold.valen.format.cast;

import java.io.*;
import java.nio.file.*;

public class Main {
    public static void main(String[] args) throws IOException {
        Cast cast = CastReader.read(Path.of("D:\\Software\\Henri-v1.1.8\\exported_files\\models\\hairs\\indiana_wear_head.cast"));
        CastNode rootNode = cast.getFirst();

        System.out.println(rootNode);
    }
}
