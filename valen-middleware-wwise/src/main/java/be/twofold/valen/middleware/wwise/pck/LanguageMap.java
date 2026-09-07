package be.twofold.valen.middleware.wwise.pck;

import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.nio.charset.*;
import java.util.*;

public record LanguageMap(
    Map<Integer, String> languages
) {
    public LanguageMap {
        languages = Map.copyOf(languages);
    }

    public static LanguageMap read(BinarySource source, int size) throws IOException {
        if (size == 0) {
            return new LanguageMap(Map.of());
        }

        // Relative indices, so slice
        var slice = source.slice(source.position(), size);
        source.skip(size);

        var count = slice.readInt();
        var offsets = new int[count];
        var ids = new int[count];
        for (var i = 0; i < count; i++) {
            offsets[i] = slice.readInt();
            ids[i] = slice.readInt();
        }

        var languages = new HashMap<Integer, String>();
        for (var i = 0; i < count; i++) {
            slice.position(offsets[i]);
            languages.put(ids[i], slice.readString(StringFormat.NULL_TERM, StandardCharsets.UTF_16LE));
        }

        return new LanguageMap(languages);
    }

    public String name(int languageId) {
        return languages.get(languageId);
    }
}
