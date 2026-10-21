package be.twofold.valen.game.eternal;

import be.twofold.valen.middleware.wwise.info.*;
import be.twofold.valen.middleware.wwise.shared.*;
import com.google.gson.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

final class MediaNamesExporter {
    private static final Path PATH = Path.of("D:\\Games\\Steam\\steamapps\\common\\DOOMEternal\\idStudio\\base\\doom-mod-000\\base\\sound\\soundbanks\\SoundbanksInfo.xml");

    private static final Gson GSON = new GsonBuilder()
        .disableHtmlEscaping()
        .setPrettyPrinting()
        .create();

    static void main() throws IOException {
        var info = InfoXmlReader.load(PATH);

        var files = new HashMap<MediaId, String>();
        for (var file : info.streamedFiles()) {
            var shortName = file.shortName()
                .replace('\\', '/')
                .replaceFirst("\\.wav$", ".wem");
            var existing = files.putIfAbsent(file.id(), shortName);
            if (existing != null && !existing.equals(shortName)) {
                System.out.println("Duplicate: " + file);
            }
        }

        var object = new JsonObject();
        files.entrySet().stream()
            .sorted(Map.Entry.<MediaId, String>comparingByValue()
                .thenComparing(e -> e.getKey().value(), Integer::compareUnsigned))
            .forEach(entry -> object.addProperty(entry.getKey().toString(), entry.getValue()));

        try (var out = Files.newBufferedWriter(Path.of("valen-game-eternal/src/main/resources/MediaNames.json"))) {
            GSON.toJson(object, out);
        }
    }
}
