package be.twofold.valen.format.cast;

import be.twofold.valen.format.cast.node.*;
import be.twofold.valen.format.cast.property.*;

import java.util.*;
import java.util.stream.*;

final class TypeClassWriter {
    public static void generate(List<TypeDef> types) {
        var enumLookup = generateEnums(types);
        for (var type : types) {
            System.out.println(generateClass(type, enumLookup));
        }
    }

    private static String generateClass(TypeDef type, Map<List<String>, String> enumLookup) {
        var builder = new StringBuilder();
        var className = className(type.type());
        var indexedProperties = type.properties().stream()
            .filter(prop -> prop.key().contains("%d"))
            .toList();

        builder.append("public static final class ").append(className).append(" extends CastNode {\n");

        for (var property : indexedProperties) {
            builder.append("    private int ").append(indexName(property)).append(";\n");
        }
        if (!indexedProperties.isEmpty()) {
            builder.append('\n');
        }

        builder.append("    ").append(className).append("(AtomicLong hasher) {\n");
        builder.append("        super(CastNodeID.").append(type.type()).append(", hasher);\n");
        builder.append("    }\n\n");

        builder.append("    ").append(className).append("(long hash, Map<String, CastProperty> properties, List<CastNode> children) {\n");
        builder.append("        super(CastNodeID.").append(type.type()).append(", hash, properties, children);\n");
        builder.append("        // TODO: Validation\n");
        builder.append("    }\n\n");

        for (var child : type.children()) {
            var childName = className(child);
            var returnType = child == CastNodeID.SKELETON ? "Optional<SkeletonNode>" : "List<" + childName + '>';
            var methodName = child == CastNodeID.SKELETON ? "getChildOfType" : "getChildrenOfType";
            builder.append("    public ").append(returnType).append(" get").append(multiple(childName)).append("() {\n");
            builder.append("        return ").append(methodName).append('(').append(childName).append(".class);\n");
            builder.append("    }\n\n");

            builder.append("    public ").append(childName).append(" create").append(childName).append("() {\n");
            builder.append("        return createChild(new ").append(childName).append("(hasher));\n");
            builder.append("    }\n\n");
        }

        for (var property : type.properties()) {
            var name = wordsToCamelCase(property.name(), true);
            var typeString = propertyType(property, enumLookup);
            var returnType = makeOptional(property, typeString);
            var getMapper = getGetMapper(property, typeString);
            var required = property.required() ? ".orElseThrow()" : "";
            var setterName = Character.toLowerCase(name.charAt(0)) + name.substring(1);
            var key = '"' + (property.isIndexed() ? property.key().replace("%d", "") : property.key()) + '"';
            var getKey = property.isIndexed() ? key + " + index" : key;

            builder.append("    public ").append(returnType).append(" get").append(name).append("(").append(property.isIndexed() ? "int index" : "").append(") {\n");
            builder.append("        return getProperty(").append(getKey).append(", ").append(getMapper).append(")").append(required).append(";\n");
            builder.append("    }\n\n");

            var setKey = property.isIndexed() ? key + " + " + indexName(property) + "++" : key;
            builder.append("    public ").append(className).append(property.isIndexed() ? " add" : " set").append(name).append("(").append(typeString).append(" ").append(setterName).append(") {\n");
            if (property.isSingular()) {
                var setMapper = getSetMapper(property, setterName);
                builder.append("        createProperty(CastPropertyID.").append(property.types().iterator().next()).append(", ").append(setKey).append(", ").append(setMapper).append(");\n");
            } else if (property.types().equals(EnumSet.of(CastPropertyID.BYTE, CastPropertyID.SHORT, CastPropertyID.INT))) {
                if (property.isArray()) {
                    builder.append("        createIntBufferProperty(").append(setKey).append(", ").append(setterName).append(");\n");
                } else {
                    builder.append("        createIntProperty(").append(setKey).append(", ").append(setterName).append(");\n");
                }
            } else {
                builder.append("        ");
                for (var propertyID : property.types()) {
                    var instanceType = property.isArray() ? arrayType(propertyID) : singularType(propertyID);
                    builder.append("if (").append(setterName).append(" instanceof ").append(instanceType).append(") {\n");
                    builder.append("            createProperty(CastPropertyID.").append(propertyID).append(", ").append(setKey).append(", ").append(setterName).append(");\n");
                    builder.append("        } else ");
                }
                builder.append("{\n");
                builder.append("            throw new IllegalArgumentException(\"Invalid type for property ").append(setterName).append("\");\n");
                builder.append("        }\n");
            }
            builder.append("        return this;\n");
            builder.append("    }\n\n");
        }

        builder.append("}\n\n");
        return builder.toString();
    }

    private static String getGetMapper(PropertyDef property, String typeString) {
        if (property.isEnum()) {
            return typeString + "::from";
        }
        if (property.isBoolean()) {
            return "this::parseBoolean";
        }
        return typeString + ".class::cast";
    }

    private static String getSetMapper(PropertyDef property, String setterName) {
        if (property.isEnum()) {
            return setterName + ".toString().toLowerCase()";
        }
        if (property.isBoolean()) {
            return setterName + " ? 1 : 0";
        }
        return setterName;
    }

    private static String propertyType(PropertyDef property, Map<List<String>, String> enumLookup) {
        if (property.isSingular()) {
            var propertyID = property.types().iterator().next();
            if (property.isArray()) {
                return arrayType(propertyID);
            } else if (property.isBoolean()) {
                return "Boolean";
            } else if (property.isEnum()) {
                return wordsToCamelCase(enumLookup.get(property.values()), true);
            } else {
                return singularType(propertyID);
            }
        }

        // Multiple types and it's an array, that's just some form of buffer
        if (property.isArray()) {
            return "Buffer";
        }

        // Stupid special cases
        var types = EnumSet.copyOf(property.types());
        if (types.equals(EnumSet.of(CastPropertyID.VECTOR3, CastPropertyID.VECTOR4))) {
            return "Object";
        } else if (types.equals(EnumSet.of(CastPropertyID.BYTE, CastPropertyID.SHORT, CastPropertyID.INT))) {
            return "Integer";
        } else {
            throw new IllegalArgumentException(types.toString());
        }
    }

    private static String singularType(CastPropertyID propertyID) {
        return switch (propertyID) {
            case BYTE -> "Byte";
            case SHORT -> "Short";
            case INT -> "Integer";
            case LONG -> "Long";
            case FLOAT -> "Float";
            case DOUBLE -> "Double";
            case STRING -> "String";
            case VECTOR2 -> "Vec2";
            case VECTOR3 -> "Vec3";
            case VECTOR4 -> "Vec4";
        };
    }

    private static String arrayType(CastPropertyID propertyID) {
        return switch (propertyID) {
            case BYTE -> "ByteBuffer";
            case SHORT -> "ShortBuffer";
            case INT -> "IntBuffer";
            case LONG -> "LongBuffer";
            case FLOAT, VECTOR2, VECTOR3, VECTOR4 -> "FloatBuffer";
            case DOUBLE -> "DoubleBuffer";
            case STRING -> throw new IllegalArgumentException();
        };
    }

    private static String makeOptional(PropertyDef property, String type) {
        if (property.required()) {
            return switch (type) {
                case "Boolean" -> "boolean";
                case "Byte" -> "byte";
                case "Character" -> "char";
                case "Double" -> "double";
                case "Float" -> "float";
                case "Integer" -> "int";
                case "Long" -> "long";
                case "Short" -> "short";
                default -> type;
            };
        }
        return "Optional<" + type + ">";
    }

    // region Enums

    private static Map<List<String>, String> generateEnums(List<TypeDef> types) {
        var enumLookup = buildEnumLookup(types);
        for (var entry : enumLookup.entrySet()) {
            generateEnum(entry.getValue(), entry.getKey());
        }
        return enumLookup;
    }

    private static void generateEnum(String name, List<String> values) {
        name = wordsToCamelCase(name, true);

        var builder = new StringBuilder();
        builder.append("public enum ").append(name).append(" {\n");

        for (var value : values) {
            builder.append("    ").append(value.toUpperCase()).append(",\n");
        }

        builder.append("    ;\n");
        builder.append('\n');
        builder.append("    public static ").append(name).append(" from(Object o) {\n");
        builder.append("        return valueOf(o.toString().toUpperCase());\n");
        builder.append("    }\n");
        builder.append("}\n");
        System.out.println(builder);
    }

    private static Map<List<String>, String> buildEnumLookup(List<TypeDef> types) {
        var result = new HashMap<List<String>, String>();
        for (var type : types) {
            for (var property : type.properties()) {
                var values = property.values();
                if (values.isEmpty() || values.equals(List.of("True", "False"))) {
                    continue;
                }
                var name = property.name();
                if (result.containsKey(values) && !result.get(values).equals(name)) {
                    throw new IllegalArgumentException("not unique");
                }
                result.put(values, name);
            }
        }
        return result;
    }

    // endregion

    private static String multiple(String name) {
        if (name.endsWith("sh")) {
            return name + "es";
        }
        return name + 's';
    }

    private static String className(CastNodeID child) {
        var result = Arrays.stream(child.name().split("_"))
            .map(s -> changeFirst(s.toLowerCase(), true))
            .collect(Collectors.joining());

        return changeFirst(result, true) + "Node";
    }

    private static String indexName(PropertyDef property) {
        return wordsToCamelCase(property.name(), false) + "Index";
    }

    private static String wordsToCamelCase(String s, boolean upper) {
        String result = String.join("", s.split("\\s+"));
        return changeFirst(result, upper);
    }

    private static String changeFirst(String s, boolean upper) {
        char first = upper
            ? Character.toUpperCase(s.charAt(0))
            : Character.toLowerCase(s.charAt(0));
        return first + s.substring(1);
    }
}
