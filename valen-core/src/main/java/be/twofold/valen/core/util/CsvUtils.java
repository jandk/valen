package be.twofold.valen.core.util;

import java.lang.reflect.*;
import java.util.*;

public final class CsvUtils {
    private CsvUtils() {
    }

    public static <T> String toCsv(List<String> names, List<T> values, Class<T> clazz) throws ReflectiveOperationException {
        var builder = new StringBuilder();
        var components = clazz.getRecordComponents();

        appendHeader(builder, components, names.size() == values.size());
        appendValues(builder, components, names, values);

        return builder.toString();
    }

    private static void appendHeader(StringBuilder builder, RecordComponent[] components, boolean hasNames) {
        if (hasNames) {
            builder.append("filename").append(',');
        }

        builder.append(components[0].getName());
        for (var i = 1; i < components.length; i++) {
            builder.append(',').append(components[i].getName());
        }
        builder.append("\r\n");
    }

    private static <T> void appendValues(
        StringBuilder builder,
        RecordComponent[] components,
        List<String> names,
        List<T> values
    ) throws ReflectiveOperationException {
        for (var i = 0; i < values.size(); i++) {
            var element = values.get(i);
            if (element == null) {
                continue;
            }
            if (names.size() == values.size()) {
                builder.append(toString(names.get(i))).append(',');
            }

            for (int j = 0; j < components.length; j++) {
                var s = toString(components[j].getAccessor().invoke(element));
                if (j != 0) {
                    builder.append(',');
                }
                builder.append(s);
            }
            builder.append("\r\n");
        }
    }

    private static String toString(Object value) {
        var s = switch (value) {
            case Collection<?> ignored -> "";
            case Map<?, ?> ignored -> "";
            case byte[] bytes -> Arrays.toString(bytes);
            case short[] shorts -> Arrays.toString(shorts);
            case int[] ints -> Arrays.toString(ints);
            case long[] longs -> Arrays.toString(longs);
            case float[] floats -> Arrays.toString(floats);
            case double[] doubles -> Arrays.toString(doubles);
            default -> Objects.toString(value);
        };
        return s.contains(",") ? "\"" + s + "\"" : s;
    }
}
