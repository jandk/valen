package be.twofold.valen.core.util.collect;

import com.squareup.javapoet.*;
import org.jetbrains.annotations.*;

import javax.lang.model.element.*;
import java.io.*;
import java.nio.*;
import java.nio.file.*;
import java.util.*;

final class WrapperGenerator {
    public static final ClassName CHECK_CLASS = ClassName.get("be.twofold.valen.core.util", "Check");
    public static final ClassName BYTE_ARRAYS_CLASS = ClassName.get("be.twofold.valen.core.util", "ByteArrays");

    public static void main(String[] args) throws IOException {
        generate("Bytes", byte.class, Byte.class, ByteBuffer.class, true);
        generate("Shorts", short.class, Short.class, ShortBuffer.class, false);
        generate("Ints", int.class, Integer.class, IntBuffer.class, false);
        generate("Longs", long.class, Long.class, LongBuffer.class, false);
        generate("Floats", float.class, Float.class, FloatBuffer.class, false);
        generate("Doubles", double.class, Double.class, DoubleBuffer.class, false);
    }

    private static void generate(
        String className,
        Class<?> primitiveClass,
        Class<?> wrapperClass,
        Class<?> bufferClass,
        boolean addExtraMethods
    ) throws IOException {
        writeClass(createWrapperClass(className, primitiveClass, wrapperClass, bufferClass, addExtraMethods));
        writeClass(createMutableWrapperClass(className, primitiveClass, wrapperClass, bufferClass));
    }

    private static TypeSpec createWrapperClass(
        String className,
        Class<?> primitiveClass,
        Class<?> wrapperClass,
        Class<?> bufferClass,
        boolean addExtraMethods
    ) {
        var thisType = ClassName.get("", className);
        var arrayType = ArrayTypeName.of(TypeName.get(primitiveClass));
        var wrapperType = TypeName.get(wrapperClass);

        var builder = TypeSpec.classBuilder(className)
            .addModifiers(Modifier.PUBLIC)
            .addSuperinterface(ParameterizedTypeName.get(ClassName.get(Comparable.class), thisType))
            .addSuperinterface(RandomAccess.class)
            .addAnnotation(AnnotationSpec.builder(Debug.Renderer.class)
                .addMember("childrenArray", "$S", "java.util.Arrays.copyOfRange(array, fromIndex, toIndex)")
                .build());

        // Fields
        builder.addField(FieldSpec.builder(arrayType, "array", Modifier.FINAL).build());
        builder.addField(FieldSpec.builder(int.class, "fromIndex", Modifier.FINAL).build());
        builder.addField(FieldSpec.builder(int.class, "toIndex", Modifier.FINAL).build());

        // Constructor
        builder.addMethod(MethodSpec.constructorBuilder()
            .addParameter(arrayType, "array")
            .addParameter(int.class, "fromIndex")
            .addParameter(int.class, "toIndex")
            .addStatement("$T.fromToIndex(fromIndex, toIndex, array.length)", CHECK_CLASS)
            .addStatement("this.array = array")
            .addStatement("this.fromIndex = fromIndex")
            .addStatement("this.toIndex = toIndex")
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
            .addParameter(int.class, "fromIndex")
            .addParameter(int.class, "toIndex")
            .addStatement("return new $L(array, fromIndex, toIndex)", className)
            .build());

        // from method
        var bufferMethodName = bufferClass.getSimpleName().replace("Buffer", "");
        builder.addMethod(MethodSpec.methodBuilder("from")
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .returns(thisType)
            .addParameter(bufferClass, "buffer")
            .addStatement("$T.argument(buffer.hasArray(), \"buffer must be backed by an array\")", CHECK_CLASS)
            .addStatement("return new $L(buffer.array(), buffer.position(), buffer.limit())", className)
            .build());

        // getPrimitive method
        builder.addMethod(MethodSpec.methodBuilder("get" + bufferMethodName)
            .addModifiers(Modifier.PUBLIC)
            .returns(primitiveClass)
            .addParameter(int.class, "index")
            .addStatement("$T.index(index, size())", CHECK_CLASS)
            .addStatement("return array[fromIndex + index]")
            .build());

        // Add extra methods for Bytes
        if (addExtraMethods) {
            addExtraBytesMethods(builder);
        }

        // asBuffer method
        builder.addMethod(MethodSpec.methodBuilder("asBuffer")
            .addModifiers(Modifier.PUBLIC)
            .returns(bufferClass)
            .addStatement("return $T.wrap(array, fromIndex, size()).asReadOnlyBuffer()", bufferClass)
            .build());

        // copyTo method
        builder.addMethod(MethodSpec.methodBuilder("copyTo")
            .addModifiers(Modifier.PUBLIC)
            .returns(void.class)
            .addParameter(ClassName.get("", "Mutable" + className), "target")
            .addParameter(int.class, "offset")
            .addStatement("$T.arraycopy(array, fromIndex, target.array, target.fromIndex + offset, size())", System.class)
            .build());

        // slice methods
        builder.addMethod(MethodSpec.methodBuilder("slice")
            .addModifiers(Modifier.PUBLIC)
            .returns(thisType)
            .addParameter(int.class, "fromIndex")
            .addStatement("return slice(fromIndex, size())")
            .build());

        builder.addMethod(MethodSpec.methodBuilder("slice")
            .addModifiers(Modifier.PUBLIC)
            .returns(thisType)
            .addParameter(int.class, "fromIndex")
            .addParameter(int.class, "toIndex")
            .addStatement("$T.fromToIndex(fromIndex, toIndex, size())", CHECK_CLASS)
            .addStatement("return new $L(array, this.fromIndex + fromIndex, this.fromIndex + toIndex)", thisType)
            .build());

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
        // size method
        builder.addMethod(MethodSpec.methodBuilder("size")
            .addModifiers(Modifier.PUBLIC)
            .returns(int.class)
            .addStatement("return toIndex - fromIndex")
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
            .beginControlFlow("for (int i = fromIndex; i < toIndex; i++)")
            .beginControlFlow("if (" + generateEquals("array[i]", "value", primitiveClass) + ")")
            .addStatement("return i - fromIndex")
            .endControlFlow()
            .endControlFlow()
            .addStatement("return -1")
            .build());

        // lastIndexOf method
        builder.addMethod(MethodSpec.methodBuilder("lastIndexOf")
            .addModifiers(Modifier.PUBLIC)
            .returns(int.class)
            .addParameter(primitiveClass, "value")
            .beginControlFlow("for (int i = toIndex - 1; i >= fromIndex; i--)")
            .beginControlFlow("if (" + generateEquals("array[i]", "value", primitiveClass) + ")")
            .addStatement("return i - fromIndex")
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
            .addStatement("return $T.compare(array, fromIndex, toIndex, o.array, o.fromIndex, o.toIndex)", Arrays.class)
            .build());

        // equals method
        builder.addMethod(MethodSpec.methodBuilder("equals")
            .addModifiers(Modifier.PUBLIC)
            .addAnnotation(Override.class)
            .returns(boolean.class)
            .addParameter(Object.class, "obj")
            .addStatement("return obj instanceof $L o && $T.equals(array, fromIndex, toIndex, o.array, o.fromIndex, o.toIndex)", className, Arrays.class)
            .build());

        // hashCode method
        builder.addMethod(MethodSpec.methodBuilder("hashCode")
            .addModifiers(Modifier.PUBLIC)
            .addAnnotation(Override.class)
            .returns(int.class)
            .addStatement("int result = 1")
            .beginControlFlow("for (int i = fromIndex; i < toIndex; i++)")
            .addStatement("result = 31 * result + $T.hashCode(array[i])", wrapperType)
            .endControlFlow()
            .addStatement("return result")
            .build());

        // toString method
        builder.addMethod(MethodSpec.methodBuilder("toString")
            .addModifiers(Modifier.PUBLIC)
            .addAnnotation(Override.class)
            .returns(String.class)
            .beginControlFlow("if (fromIndex == toIndex)")
            .addStatement("return \"[]\"")
            .endControlFlow()
            .addStatement("StringBuilder builder = new StringBuilder()")
            .addStatement("builder.append('[').append(array[fromIndex])")
            .beginControlFlow("for (int i = fromIndex + 1; i < toIndex; i++)")
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

    private static void addExtraBytesMethods(TypeSpec.Builder classBuilder) {
        generateAccess(classBuilder, short.class, "Short", "Short.BYTES");
        generateAccess(classBuilder, int.class, "Int", "Integer.BYTES");
        generateAccess(classBuilder, long.class, "Long", "Long.BYTES");
        generateAccess(classBuilder, float.class, "Float", "Float.BYTES");
        generateAccess(classBuilder, double.class, "Double", "Double.BYTES");
        generateUnsignedAccess(classBuilder, int.class, "Byte", "Byte.toUnsignedInt");
        generateUnsignedAccess(classBuilder, int.class, "Short", "Short.toUnsignedInt");
        generateUnsignedAccess(classBuilder, long.class, "Int", "Integer.toUnsignedLong");
    }

    private static void generateAccess(TypeSpec.Builder classBuilder, Class<?> primitive, String upper, String size) {
        classBuilder.addMethod(MethodSpec.methodBuilder("get" + upper)
            .addModifiers(Modifier.PUBLIC)
            .returns(primitive)
            .addParameter(int.class, "offset")
            .addStatement("$T.fromIndexSize(offset, $L, size())", CHECK_CLASS, size)
            .addStatement("return $T.get$L(array, fromIndex + offset)", BYTE_ARRAYS_CLASS, upper)
            .build());
    }

    private static void generateUnsignedAccess(TypeSpec.Builder classBuilder, Class<?> primitive, String upper, String conv) {
        classBuilder.addMethod(MethodSpec.methodBuilder("getUnsigned" + upper)
            .addModifiers(Modifier.PUBLIC)
            .returns(primitive)
            .addParameter(int.class, "offset")
            .addStatement("return $L(get$L(offset))", conv, upper)
            .build());
    }

    private static TypeSpec createMutableWrapperClass(
        String className,
        Class<?> primitiveClass,
        Class<?> wrapperClass,
        Class<?> bufferClass
    ) {
        var primitiveArrayType = ArrayTypeName.of(TypeName.get(primitiveClass));
        var wrapperTypeName = TypeName.get(wrapperClass);
        var bufferMethodName = bufferClass.getSimpleName().replace("Buffer", "");

        var builder = TypeSpec.classBuilder("Mutable" + className)
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
            .superclass(ClassName.get("", className));

        builder.addMethod(MethodSpec.constructorBuilder()
            .addModifiers(Modifier.PRIVATE)
            .addParameter(primitiveArrayType, "array")
            .addParameter(int.class, "fromIndex")
            .addParameter(int.class, "toIndex")
            .addStatement("super(array, fromIndex, toIndex)")
            .build());

        builder.addMethod(MethodSpec.methodBuilder("wrap")
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .returns(ClassName.get("", "Mutable" + className))
            .addParameter(primitiveArrayType, "array")
            .addStatement("return new Mutable$L(array, 0, array.length)", className)
            .build());

        builder.addMethod(MethodSpec.methodBuilder("wrap")
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .returns(ClassName.get("", "Mutable" + className))
            .addParameter(primitiveArrayType, "array")
            .addParameter(int.class, "fromIndex")
            .addParameter(int.class, "toIndex")
            .addStatement("return new Mutable$L(array, fromIndex, toIndex)", className)
            .build());

        builder.addMethod(MethodSpec.methodBuilder("allocate")
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .returns(ClassName.get("", "Mutable" + className))
            .addParameter(int.class, "size")
            .addStatement("return new Mutable$L(new $L[size], 0, size)", className, primitiveClass)
            .build());

        builder.addMethod(MethodSpec.methodBuilder("set" + bufferMethodName)
            .addModifiers(Modifier.PUBLIC)
            .returns(void.class)
            .addParameter(int.class, "index")
            .addParameter(primitiveClass, "value")
            .addStatement("$T.index(index, size())", CHECK_CLASS)
            .addStatement("array[fromIndex + index] = value")
            .build());

        builder.addMethod(MethodSpec.methodBuilder("asMutableBuffer")
            .addModifiers(Modifier.PUBLIC)
            .returns(bufferClass)
            .addStatement("return $T.wrap(array, fromIndex, size())", bufferClass)
            .build());

        return builder.build();
    }

    private static void writeClass(TypeSpec typeSpec) throws IOException {
        JavaFile
            .builder("be.twofold.valen.core.util.collect", typeSpec)
            .indent("    ")
            .build()
            .writeTo(Path.of("valen-core/src/main/java"));
    }
}
