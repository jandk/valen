package be.twofold.valen.format.cast;

import java.util.*;
import java.util.regex.*;
import java.util.stream.*;

final class TypeParser {
    private static final Pattern PART0_PATTERN = Pattern.compile("(.+?) (?:\\([^(]+)?\\(([\\w%]+)\\)");
    private static final Pattern PART1_PATTERN = Pattern.compile("[^(]+\\((\\w+)\\)(?:\\s+\\[([^]]+)])?");

    static TypeDef parse(RawType rawType) {
        List<CastNodeID> children = !rawType.children().isEmpty()
            ? Arrays.stream(rawType.children().split(","))
            .map(s -> parseType(s.strip()))
            .toList()
            : List.<CastNodeID>of();

        List<PropertyDef> properties = rawType.properties().lines()
            .map(TypeParser::parseProperty)
            .toList();

        return new TypeDef(
            parseType(rawType.name()),
            children,
            properties
        );
    }

    private static CastNodeID parseType(String s) {
        String name = Arrays.stream(s.split("\\s+"))
            .map(String::toUpperCase)
            .collect(Collectors.joining("_"));
        return CastNodeID.valueOf(name);
    }

    private static PropertyDef parseProperty(String s) {
        List<String> parts = Arrays.stream(s.split("\t"))
            .map(String::strip)
            .toList();


        NameAndKey part0 = parsePropertyName(parts.get(0));
        TypesAndValues part1 = parseTypesAndValues(parts.get(1));
        boolean part2 = parsePropertyBoolean(parts.get(2));
        boolean part3 = parsePropertyBoolean(parts.get(3));

        return new PropertyDef(
            part0.name(),
            part0.key(),
            part1.types(),
            part1.values(),
            part2,
            part3
        );
    }

    private static NameAndKey parsePropertyName(String s) {
        Matcher matcher = PART0_PATTERN.matcher(s);
        if (!matcher.matches()) {
            throw new IllegalArgumentException();
        }
        return new NameAndKey(matcher.group(1), matcher.group(2));
    }

    private static TypesAndValues parseTypesAndValues(String s) {
        Matcher matcher = PART1_PATTERN.matcher(s);

        EnumSet<CastPropertyID> allTypes = EnumSet.noneOf(CastPropertyID.class);
        ArrayList<String> allValues = new ArrayList<String>();
        while (matcher.find()) {
            Collection<String> values = matcher.group(2) != null
                ? Arrays.stream(matcher.group(2).split(",")).map(String::strip).collect(Collectors.toSet())
                : List.<String>of();
            if (!values.isEmpty() && !allValues.isEmpty()) {
                throw new IllegalArgumentException();
            }

            allTypes.add(stringToType(matcher.group(1)));
            allValues.addAll(values);
        }
        return new TypesAndValues(allTypes, allValues);
    }

    private static boolean parsePropertyBoolean(String s) {
        if (s.indexOf(' ') >= 0) {
            return false;
        }
        return switch (s) {
            case "True" -> true;
            case "False" -> false;
            default -> throw new IllegalArgumentException();
        };
    }

    private static CastPropertyID stringToType(String s) {
        return switch (s) {
            case "b" -> CastPropertyID.BYTE;
            case "h" -> CastPropertyID.SHORT;
            case "i" -> CastPropertyID.INT;
            case "l" -> CastPropertyID.LONG;
            case "f" -> CastPropertyID.FLOAT;
            case "d" -> CastPropertyID.DOUBLE;
            case "s" -> CastPropertyID.STRING;
            case "v2" -> CastPropertyID.VECTOR2;
            case "v3" -> CastPropertyID.VECTOR3;
            case "v4" -> CastPropertyID.VECTOR4;
            default -> throw new IllegalArgumentException();
        };
    }

    private record NameAndKey(
        String name,
        String key
    ) {
    }

    private record TypesAndValues(
        Set<CastPropertyID> types,
        List<String> values
    ) {
        private TypesAndValues {
            types = EnumSet.copyOf(types);
            values = List.copyOf(values);
        }
    }
}
