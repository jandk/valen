package be.twofold.valen.reader.decl;

import be.twofold.valen.oodle.*;
import be.twofold.valen.reader.decl.model.*;
import be.twofold.valen.reader.decl.parser.*;
import com.google.gson.*;

import java.io.*;
import java.nio.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;

public class DeclHandler {

    public static final Path DeclRoot = Path.of("C:\\Temp\\DOOM\\generated\\decls\\material2");

    public static void main(String[] args) throws IOException {
        Path path = Path.of("D:\\Jan\\Desktop\\e1m1_intro.entities.oodle");
        ByteBuffer buffer = ByteBuffer.wrap(Files.readAllBytes(path));
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        long l1 = buffer.getLong();
        long l2 = buffer.getLong();
        System.out.println(l1);
        System.out.println(l2);

        byte[] compressed = Arrays.copyOfRange(buffer.array(), buffer.position(), buffer.limit());
        byte[] decompressed = OodleDecompressor.decompress(compressed, (int) l1);
        String decoded = StandardCharsets.US_ASCII.decode(ByteBuffer.wrap(decompressed)).toString();

//         String s = Files.readString(Path.of("C:\\Temp\\DOOMExtracted\\generated\\decls\\material2\\art\\weapons\\heavycannon\\heavy_base_front.decl"));
        String s = decoded;
        DeclValue value = new DeclParser(s).parse();
        System.out.println(value);

        JsonElement json = toJson(value);
        new GsonBuilder().setPrettyPrinting().create().toJson(json, System.out);

//        DeclObject result = load(DeclRoot.resolve("art\\weapons\\heavycannon\\heavy_base_front.decl"));
//
//        Set<String> rootKeys = new HashSet<>();
//        Files.walkFileTree(DeclRoot, new SimpleFileVisitor<>() {
//            @Override
//            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
//                if (file.getFileName().toString().endsWith(".decl")) {
//                    String declString = Files.readString(file);
//                    DeclValue value = DeclParser.parse(declString);
//
//                    DeclValue edit = value.asObject().get("edit");
//                    if (edit != null) {
//                        DeclValue passes = edit.asObject().get("Passes");
//                        if (passes != null) {
//                            passes.asObject().forEach(e -> rootKeys.add(e.getKey()));
//                        }
//                    }
//                }
//                return FileVisitResult.CONTINUE;
//            }
//        });
    }

    private static DeclObject load(Path path) {
        return load0(path).get("edit").asObject();
    }

    private static DeclObject load0(Path path) {
//        DeclObject decl = DeclParser.load(path).asObject();
//
//        if (decl.get("inherit") != null) {
//            decl = load0(DeclRoot.resolve(decl.get("inherit").asString() + ".decl")).merge(decl);
//        }
//
//        return decl;
        return null;
    }


    private static JsonElement toJson(DeclValue value) {
        if (value.isObject()) {
            JsonObject object = new JsonObject();
            for (Map.Entry<String, DeclValue> entry : value.asObject()) {
                object.add(entry.getKey(), toJson(entry.getValue()));
            }
            return object;
        }
        if (value.isString()) {
            return new JsonPrimitive(value.asString());
        }
        if (value.isNumber()) {
            return new JsonPrimitive(value.asNumber());
        }
        if (value.isBoolean()) {
            return new JsonPrimitive(value.asBoolean());
        }
        throw new IllegalStateException("Unknown value type: " + value);
    }

}
