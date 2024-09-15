package be.twofold.valen.core.util;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

public final class CsvUtils {
    public static <T> String toCsv(List<String> names, List<T> values, Class<T> clazz) throws IOException {
        var builder = new StringBuilder();
        var components = clazz.getRecordComponents();
        builder.append("filename");
        for (var component : components) {
            builder.append(',').append(component.getName());
        }
        builder.append('\n');

        for (var i = 0; i < values.size(); i++) {
            var element = values.get(i);
            if (element == null) {
                continue;
            }
            if (names.size() == values.size()) {
                builder.append(names.get(i));
            }
            for (var component : components) {
                Object object;
                try {
                    object = component.getAccessor().invoke(element);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new IOException("Failed to get value for " + component.getName(), e);
                }
                var s = switch (object) {
                    case List<?> ignored -> "";
                    case byte[] bytes -> Arrays.toString(bytes);
                    case short[] shorts -> Arrays.toString(shorts);
                    case int[] ints -> Arrays.toString(ints);
                    case int[][] ints -> Arrays.deepToString(ints);
                    case float[] floats -> Arrays.toString(floats);
                    default -> Objects.toString(object);
                };
                builder.append(',').append(s.contains(",") ? "\"" + s + "\"" : s);
            }
            builder.append('\n');
        }
        return builder.toString();
    }
}
