package be.twofold.valen.core.util.collect;

import com.squareup.javapoet.*;
import org.jetbrains.annotations.*;

import javax.lang.model.element.*;
import java.io.*;
import java.lang.invoke.*;
import java.nio.*;
import java.nio.file.*;
import java.util.*;

final class ArrayGenerator {
    private static final ClassName CHECK_CLASS = ClassName.get("be.twofold.valen.core.util", "Check");
    private static final String PARENT_CLASS_NAME = "Array";

    public static void main(String[] args) throws IOException {
        generateInterface();
        generate("Bytes", byte.class, Byte.class, ByteBuffer.class);
        generate("Shorts", short.class, Short.class, ShortBuffer.class);
        generate("Ints", int.class, Integer.class, IntBuffer.class);
        generate("Longs", long.class, Long.class, LongBuffer.class);
        generate("Floats", float.class, Float.class, FloatBuffer.class);
        generate("Doubles", double.class, Double.class, DoubleBuffer.class);
    }

    private static void generateInterface() throws IOException {
        var typeSpec = TypeSpec.interfaceBuilder(PARENT_CLASS_NAME)
            .addModifiers(Modifier.PUBLIC)
            .addMethod(MethodSpec.methodBuilder("length")
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .returns(int.class)
                .build())
            .addMethod(MethodSpec.methodBuilder("asBuffer")
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .returns(Buffer.class)
                .build())
            .build();

        writeClass(typeSpec);
    }

    private static void generate(
        String className,
        Class<?> primitiveClass,
        Class<?> wrapperClass,
        Class<?> bufferClass
    ) throws IOException {
        writeClass(createWrapperClass(className, primitiveClass, wrapperClass, bufferClass));
        writeClass(createMutableWrapperClass("Mutable" + className, className, primitiveClass, bufferClass));
    }

    private static TypeSpec createWrapperClass(
        String className,
        Class<?> primitiveClass,
        Class<?> wrapperClass,
        Class<?> bufferClass
    ) {
        var thisType = ClassName.get("", className);
        var mutableType = ClassName.get("", "Mutable" + className);
        var arrayType = ArrayTypeName.of(TypeName.get(primitiveClass));
        var wrapperType = TypeName.get(wrapperClass);

        var builder = TypeSpec.classBuilder(className)
            .addModifiers(Modifier.PUBLIC)
            .addSuperinterface(ParameterizedTypeName.get(ClassName.get(Comparable.class), thisType))
            .addSuperinterface(ClassName.get("", PARENT_CLASS_NAME))
            .addAnnotation(AnnotationSpec.builder(Debug.Renderer.class)
                .addMember("childrenArray", "$S", "java.util.Arrays.copyOfRange(array, offset, offset + length)")
                .build());

        // Fields
        builder.addField(FieldSpec.builder(thisType, "EMPTY", Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
            .initializer("wrap(new $T[0])", primitiveClass)
            .build());
        if (primitiveClass == byte.class) {
            for (String s : List.of("short", "int", "long", "float", "double")) {
                builder.addField(FieldSpec.builder(VarHandle.class, "VH_" + s.toUpperCase() + "_LE", Modifier.STATIC, Modifier.FINAL)
                    .initializer("$T.byteArrayViewVarHandle($L[].class, $T.$L).withInvokeExactBehavior()", MethodHandles.class, s, ByteOrder.class, ByteOrder.LITTLE_ENDIAN)
                    .build());
            }
        }

        builder.addField(FieldSpec.builder(arrayType, "array", Modifier.FINAL).build());
        builder.addField(FieldSpec.builder(int.class, "offset", Modifier.FINAL).build());
        builder.addField(FieldSpec.builder(int.class, "length", Modifier.FINAL).build());

        // Constructor
        builder.addMethod(MethodSpec.constructorBuilder()
            .addParameter(arrayType, "array")
            .addParameter(int.class, "offset")
            .addParameter(int.class, "length")
            .addStatement("$T.fromIndexSize(offset, length, array.length)", CHECK_CLASS)
            .addStatement("this.array = array")
            .addStatement("this.offset = offset")
            .addStatement("this.length = length")
            .build());

        // Static empty method
        builder.addMethod(MethodSpec.methodBuilder("empty")
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .returns(thisType)
            .addStatement("return EMPTY")
            .build());

        // Static wrap methods
        builder.addMethod(MethodSpec.methodBuilder("wrap")
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .returns(thisType)
            .addParameter(arrayType, "array")
            .addStatement("return new $L(array, 0, array.length)", className)
            .build());

        builder.addMethod(MethodSpec.methodBuilder("wrap")
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .returns(thisType)
            .addParameter(arrayType, "array")
            .addParameter(int.class, "offset")
            .addParameter(int.class, "length")
            .addStatement("return new $L(array, offset, length)", className)
            .build());

        // Static from method
        builder.addMethod(MethodSpec.methodBuilder("from")
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .returns(thisType)
            .addParameter(bufferClass, "buffer")
            .addStatement("$T.argument(buffer.hasArray(), \"buffer must be backed by an array\")", CHECK_CLASS)
            .addStatement("return new $L(buffer.array(), buffer.position(), buffer.limit())", className)
            .build());

        // getPrimitive method
        builder.addMethod(MethodSpec.methodBuilder("get")
            .addModifiers(Modifier.PUBLIC)
            .returns(primitiveClass)
            .addParameter(int.class, "index")
            .addStatement("$T.index(index, length)", CHECK_CLASS)
            .addStatement("return array[offset + index]")
            .build());

        // Add extra methods for Primitives
        if (primitiveClass == byte.class || primitiveClass == short.class || primitiveClass == int.class) {
            addExtraPrimitivesMethods(builder, primitiveClass);
        }

        // asBuffer method
        builder.addMethod(MethodSpec.methodBuilder("asBuffer")
            .addAnnotation(Override.class)
            .addModifiers(Modifier.PUBLIC)
            .returns(bufferClass)
            .addStatement("return $T.wrap(array, offset, length).asReadOnlyBuffer()", bufferClass)
            .build());

        // copyTo method
        builder.addMethod(MethodSpec.methodBuilder("copyTo")
            .addModifiers(Modifier.PUBLIC)
            .returns(void.class)
            .addParameter(mutableType, "target")
            .addParameter(int.class, "offset")
            .addStatement("$T.arraycopy(array, this.offset, target.array, target.offset + offset, length)", System.class)
            .build());

        // slice methods
        addSliceMethods(builder, thisType);

        // Override methods
        addOverrideMethods(builder, className, wrapperType, primitiveClass, thisType);

        return builder.build();
    }

    private static void addOverrideMethods(
        TypeSpec.Builder builder,
        String className,
        TypeName wrapperType,
        Class<?> primitiveClass,
        TypeName thisType
    ) {
        // length method
        builder.addMethod(MethodSpec.methodBuilder("length")
            .addAnnotation(Override.class)
            .addModifiers(Modifier.PUBLIC)
            .returns(int.class)
            .addStatement("return length")
            .build());

        // contains method
        builder.addMethod(MethodSpec.methodBuilder("contains")
            .addModifiers(Modifier.PUBLIC)
            .returns(boolean.class)
            .addParameter(primitiveClass, "value")
            .addStatement("return indexOf(value) >= 0")
            .build());

        // indexOf method
        builder.addMethod(MethodSpec.methodBuilder("indexOf")
            .addModifiers(Modifier.PUBLIC)
            .returns(int.class)
            .addParameter(primitiveClass, "value")
            .beginControlFlow("for (int i = offset, limit = offset + length; i < limit; i++)")
            .beginControlFlow("if (" + generateEquals("array[i]", "value", primitiveClass) + ")")
            .addStatement("return i - offset")
            .endControlFlow()
            .endControlFlow()
            .addStatement("return -1")
            .build());

        // lastIndexOf method
        builder.addMethod(MethodSpec.methodBuilder("lastIndexOf")
            .addModifiers(Modifier.PUBLIC)
            .returns(int.class)
            .addParameter(primitiveClass, "value")
            .beginControlFlow("for (int i = offset + length - 1; i >= offset; i--)")
            .beginControlFlow("if (" + generateEquals("array[i]", "value", primitiveClass) + ")")
            .addStatement("return i - offset")
            .endControlFlow()
            .endControlFlow()
            .addStatement("return -1")
            .build());

        // compareTo method
        builder.addMethod(MethodSpec.methodBuilder("compareTo")
            .addModifiers(Modifier.PUBLIC)
            .addAnnotation(Override.class)
            .returns(int.class)
            .addParameter(thisType, "o")
            .addStatement("return $T.compare(array, offset, offset + length, o.array, o.offset, o.offset + o.length)", Arrays.class)
            .build());

        // equals method
        builder.addMethod(MethodSpec.methodBuilder("equals")
            .addModifiers(Modifier.PUBLIC)
            .addAnnotation(Override.class)
            .returns(boolean.class)
            .addParameter(Object.class, "obj")
            .addStatement("return obj instanceof $L o && $T.equals(array, offset, offset + length, o.array, o.offset, o.offset + o.length)", className, Arrays.class)
            .build());

        // hashCode method
        builder.addMethod(MethodSpec.methodBuilder("hashCode")
            .addModifiers(Modifier.PUBLIC)
            .addAnnotation(Override.class)
            .returns(int.class)
            .addStatement("int result = 1")
            .beginControlFlow("for (int i = offset, limit = offset + length; i < limit; i++)")
            .addStatement("result = 31 * result + $T.hashCode(array[i])", wrapperType)
            .endControlFlow()
            .addStatement("return result")
            .build());

        // toString method
        builder.addMethod(MethodSpec.methodBuilder("toString")
            .addModifiers(Modifier.PUBLIC)
            .addAnnotation(Override.class)
            .returns(String.class)
            .beginControlFlow("if (length == 0)")
            .addStatement("return \"[]\"")
            .endControlFlow()
            .addStatement("StringBuilder builder = new StringBuilder()")
            .addStatement("builder.append('[').append(array[offset])")
            .beginControlFlow("for (int i = offset + 1, limit = offset + length; i < limit; i++)")
            .addStatement("builder.append(\", \").append(array[i])")
            .endControlFlow()
            .addStatement("return builder.append(']').toString()")
            .build());
    }

    private static String generateEquals(String left, String right, Class<?> type) {
        return switch (type.getSimpleName()) {
            case "byte", "short", "int", "long" -> left + " == " + right;
            case "float" -> "Float.compare(" + left + ", " + right + ") == 0";
            case "double" -> "Double.compare(" + left + ", " + right + ") == 0";
            default -> throw new UnsupportedOperationException();
        };
    }

    private static void addExtraPrimitivesMethods(TypeSpec.Builder classBuilder, Class<?> primitiveClass) {
        if (primitiveClass == byte.class) {
            generateGet(classBuilder, short.class, "Short", "Short.BYTES");
            generateGet(classBuilder, int.class, "Int", "Integer.BYTES");
            generateGet(classBuilder, long.class, "Long", "Long.BYTES");
            generateGet(classBuilder, float.class, "Float", "Float.BYTES");
            generateGet(classBuilder, double.class, "Double", "Double.BYTES");
            generateGetUnsigned(classBuilder, int.class, "", "getUnsigned", "Byte.toUnsignedInt");
            generateGetUnsigned(classBuilder, int.class, "Short", "getUnsignedShort", "Short.toUnsignedInt");
            generateGetUnsigned(classBuilder, long.class, "Int", "getUnsignedInt", "Integer.toUnsignedLong");
        }
        if (primitiveClass == short.class) {
            generateGetUnsigned(classBuilder, int.class, "", "getUnsigned", "Short.toUnsignedInt");
        }
        if (primitiveClass == int.class) {
            generateGetUnsigned(classBuilder, long.class, "", "getUnsigned", "Integer.toUnsignedLong");
        }
    }

    private static void generateGet(TypeSpec.Builder classBuilder, Class<?> primitive, String upper, String length) {
        classBuilder.addMethod(MethodSpec.methodBuilder("get" + upper)
            .addModifiers(Modifier.PUBLIC)
            .returns(primitive)
            .addParameter(int.class, "offset")
            .addStatement("$T.fromIndexSize(offset, $L, length)", CHECK_CLASS, length)
            .addStatement("return ($T) $L.get(array, this.offset + offset)", primitive, "VH_" + primitive.getSimpleName().toUpperCase() + "_LE")
            .build());
    }

    private static void generateGetUnsigned(TypeSpec.Builder classBuilder, Class<?> primitive, String getSuffix, String name, String conv) {
        classBuilder.addMethod(MethodSpec.methodBuilder(name)
            .addModifiers(Modifier.PUBLIC)
            .returns(primitive)
            .addParameter(int.class, "offset")
            .addStatement("return $L(get$L(offset))", conv, getSuffix)
            .build());
    }

    private static void addExtraMutableBytesMethods(TypeSpec.Builder classBuilder, ClassName thisType) {
        generateSet(classBuilder, short.class, "Short", "Short.BYTES", thisType);
        generateSet(classBuilder, int.class, "Int", "Integer.BYTES", thisType);
        generateSet(classBuilder, long.class, "Long", "Long.BYTES", thisType);
        generateSet(classBuilder, float.class, "Float", "Float.BYTES", thisType);
        generateSet(classBuilder, double.class, "Double", "Double.BYTES", thisType);
    }

    private static void generateSet(TypeSpec.Builder classBuilder, Class<?> primitive, String upper, String length, ClassName thisType) {
        classBuilder.addMethod(MethodSpec.methodBuilder("set" + upper)
            .addModifiers(Modifier.PUBLIC)
            .returns(thisType)
            .addParameter(int.class, "offset")
            .addParameter(primitive, "value")
            .addStatement("$T.fromIndexSize(offset, $L, length)", CHECK_CLASS, length)
            .addStatement("$L.set(array, this.offset + offset, value)", "VH_" + primitive.getSimpleName().toUpperCase() + "_LE")
            .addStatement("return this")
            .build());
    }

    private static TypeSpec createMutableWrapperClass(
        String className,
        String baseClassName,
        Class<?> primitiveClass,
        Class<?> bufferClass
    ) {
        var thisType = ClassName.get("", className);
        var primitiveArrayType = ArrayTypeName.of(TypeName.get(primitiveClass));

        var builder = TypeSpec.classBuilder(className)
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
            .superclass(ClassName.get("", baseClassName));

        builder.addMethod(MethodSpec.constructorBuilder()
            .addModifiers(Modifier.PRIVATE)
            .addParameter(primitiveArrayType, "array")
            .addParameter(int.class, "offset")
            .addParameter(int.class, "length")
            .addStatement("super(array, offset, length)")
            .build());

        builder.addMethod(MethodSpec.methodBuilder("wrap")
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .returns(thisType)
            .addParameter(primitiveArrayType, "array")
            .addStatement("return new $L(array, 0, array.length)", className)
            .build());

        builder.addMethod(MethodSpec.methodBuilder("wrap")
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .returns(thisType)
            .addParameter(primitiveArrayType, "array")
            .addParameter(int.class, "offset")
            .addParameter(int.class, "length")
            .addStatement("return new $L(array, offset, length)", className)
            .build());

        builder.addMethod(MethodSpec.methodBuilder("allocate")
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .returns(thisType)
            .addParameter(int.class, "length")
            .addStatement("return new $L(new $L[length], 0, length)", className, primitiveClass)
            .build());

        builder.addMethod(MethodSpec.methodBuilder("set")
            .addModifiers(Modifier.PUBLIC)
            .returns(thisType)
            .addParameter(int.class, "index")
            .addParameter(primitiveClass, "value")
            .addStatement("$T.index(index, length)", CHECK_CLASS)
            .addStatement("array[offset + index] = value")
            .addStatement("return this")
            .build());

        if (primitiveClass == byte.class) {
            addExtraMutableBytesMethods(builder, thisType);
        }

        builder.addMethod(MethodSpec.methodBuilder("fill")
            .addModifiers(Modifier.PUBLIC)
            .addParameter(primitiveClass, "value")
            .returns(thisType)
            .addStatement("$T.fill(array, offset, offset + length, value)", Arrays.class)
            .addStatement("return this")
            .build());

        builder.addMethod(MethodSpec.methodBuilder("asMutableBuffer")
            .addModifiers(Modifier.PUBLIC)
            .returns(bufferClass)
            .addStatement("return $T.wrap(array, offset, length)", bufferClass)
            .build());

        addSliceMethods(builder, thisType);

        return builder.build();
    }

    private static void addSliceMethods(TypeSpec.Builder builder, ClassName thisType) {
        builder.addMethod(MethodSpec.methodBuilder("slice")
            .addModifiers(Modifier.PUBLIC)
            .returns(thisType)
            .addParameter(int.class, "offset")
            .addStatement("return slice(offset, length - offset)")
            .build());

        builder.addMethod(MethodSpec.methodBuilder("slice")
            .addModifiers(Modifier.PUBLIC)
            .returns(thisType)
            .addParameter(int.class, "offset")
            .addParameter(int.class, "length")
            .addStatement("$T.fromIndexSize(offset, length, this.length)", CHECK_CLASS)
            .addStatement("return new $L(array, this.offset + offset, length)", thisType)
            .build());
    }

    private static void writeClass(TypeSpec typeSpec) throws IOException {
        JavaFile
            .builder("be.twofold.valen.core.util.collect", typeSpec)
            .indent("    ")
            .build()
            .writeTo(Path.of("valen-core/src/main/java"));
    }
}
