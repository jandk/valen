//package be.twofold.valen.q3bsp;
//
//import be.twofold.valen.core.io.*;
//import be.twofold.valen.q3bsp.model.*;
//
//import java.io.*;
//import java.nio.file.*;
//import java.util.*;
//import java.util.stream.*;
//
//public final class Main {
//
//    public static void main(String[] args) throws IOException {
//        Path path = Path.of("D:\\Games\\Steam\\steamapps\\common\\Quake 3 Arena\\baseq3\\pak0\\maps\\q3tourney6.bsp");
//        var reader = new Q3BspReader(DataSource.fromPath(path));
//        var bsp = reader.read();
//
//        Map<Integer, Long> counts = bsp.surfaces().stream()
//            .collect(Collectors.groupingBy(Surface::surfaceType, Collectors.counting()));
//        counts.forEach((integer, aLong) -> System.out.println(integer + ": " + aLong));
//
////            for (int i = 0; i < bsp.lightMaps().size(); i++) {
////                var lightMap = bsp.lightMaps().get(i);
////                ImageIO.write(lightMap.asImage(), "png", new File("lightmap-%02d.png".formatted(i)));
////            }
//
//        System.out.println(bsp.drawVerts().size() + " drawVerts");
//    }
//}
