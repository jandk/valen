package be.twofold.valen.format.granite.gdex;

import be.twofold.valen.core.util.collect.*;

import java.util.*;

final class Stringifier {
    private final StringBuilder builder = new StringBuilder();
    private int indent = 0;

    public String stringify(Gdex gdex) {
        builder.append(gdex.tag()).append(": ");
        return stringify0(gdex).toString();
    }

    private StringBuilder stringify0(Gdex gdex) {
        switch (gdex) {
            case GdexArray<?> _ -> appendArray(gdex.asArray());
            case GdexDate _ -> builder.append(gdex.asDate());
            case GdexDouble _, GdexFloat _, GdexInt32 _, GdexInt64 _ -> builder.append(gdex.asNumber());
            case GdexGuid _ -> builder.append(gdex.asGuid());
            case GdexRaw _ -> appendRaw(gdex.asBytes());
            case GdexString _ -> appendString(gdex.asString());
            case GdexStruct _ -> appendStruct(gdex.asStruct());
        }
        return builder;
    }

    private void appendArray(List<?> values) {
        builder.append("[\n");
        indent++;
        for (var value : values) {
            indent().append(value).append(",\n");
        }
        indent--;
        indent().append(']');
    }

    private void appendRaw(Bytes bytes) {
        builder.append('[').append(bytes.length()).append("bytes ]");
    }

    private void appendString(String value) {
        if (!value.contains("\n")) {
            builder.append('"').append(value).append('"');
            return;
        }

        var count = 0;
        for (var i = 0; i < value.length(); i++) {
            if (value.charAt(i) == '\n') {
                count++;
            }
        }
        builder.append('[').append(count).append(" lines]");
    }

    private void appendStruct(GdexStruct struct) {
        builder.append("{\n");
        indent++;
        for (var value : struct.values()) {
            indent().append(value.tag()).append(": ");
            stringify0(value).append(",\n");
        }
        indent--;
        indent().append('}');
    }

    private StringBuilder indent() {
        return builder.repeat("  ", indent);
    }
}
