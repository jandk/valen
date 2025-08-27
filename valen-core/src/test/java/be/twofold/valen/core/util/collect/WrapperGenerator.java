package be.twofold.valen.core.util.collect;

import com.squareup.javapoet.*;

import javax.lang.model.element.*;
import java.io.*;
import java.nio.*;
import java.nio.file.*;
import java.util.*;

final class WrapperGenerator {
    public static final ClassName CHECK_CLASS = ClassName.get("be.twofold.valen.core.util", "Check");
    public static final ClassName BYTE_ARRAYS_CLASS = ClassName.get("be.twofold.valen.core.util", "ByteArrays");
    public static final ClassName ARRAY_UTILS_CLASS = ClassName.get("be.twofold.valen.core.util", "ArrayUtils");

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
            .superclass(ParameterizedTypeName.get(ClassName.get(AbstractList.class), wrapperType))
            .addSuperinterface(ParameterizedTypeName.get(ClassName.get(Comparable.class), thisType))
            .addSuperinterface(RandomAccess.class);

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
            .addStatement("return subList(fromIndex, size())")
            .build());

        builder.addMethod(MethodSpec.methodBuilder("slice")
            .addModifiers(Modifier.PUBLIC)
            .returns(thisType)
            .addParameter(int.class, "fromIndex")
            .addParameter(int.class, "toIndex")
            .addStatement("return subList(fromIndex, toIndex)")
            .build());

        // Override methods
        addOverrideMethods(builder, className, bufferMethodName, wrapperType, thisType);

        return builder.build();
    }

    private static void addOverrideMethods(
        TypeSpec.Builder builder,
        String className,
        String bufferMethodName,
        TypeName wrapperType,
        TypeName thisType
    ) {
        // size method
        builder.addMethod(override("size")
            .returns(int.class)
            .addStatement("return toIndex - fromIndex")
            .build());

        // get method (deprecated)
        builder.addMethod(override("get")
            .addAnnotation(Deprecated.class)
            .returns(wrapperType)
            .addParameter(int.class, "index")
            .addStatement("return get$L(index)", bufferMethodName)
            .build());

        // contains method
        builder.addMethod(override("contains")
            .returns(boolean.class)
            .addParameter(Object.class, "o")
            .addStatement("return o instanceof $L value && $T.contains(array, fromIndex, toIndex, value)", wrapperType, ARRAY_UTILS_CLASS)
            .build());

        // indexOf method
        builder.addMethod(override("indexOf")
            .returns(int.class)
            .addParameter(Object.class, "o")
            .beginControlFlow("if (o instanceof $L value)", wrapperType)
            .addStatement("int index = $T.indexOf(array, fromIndex, toIndex, value)", ARRAY_UTILS_CLASS)
            .beginControlFlow("if (index >= 0)")
            .addStatement("return index - fromIndex")
            .endControlFlow()
            .endControlFlow()
            .addStatement("return -1")
            .build());

        // lastIndexOf method
        builder.addMethod(override("lastIndexOf")
            .returns(int.class)
            .addParameter(Object.class, "o")
            .beginControlFlow("if (o instanceof $L value)", wrapperType)
            .addStatement("int index = $T.lastIndexOf(array, fromIndex, toIndex, value)", ARRAY_UTILS_CLASS)
            .beginControlFlow("if (index >= 0)")
            .addStatement("return index - fromIndex")
            .endControlFlow()
            .endControlFlow()
            .addStatement("return -1")
            .build());

        // subList method
        builder.addMethod(override("subList")
            .returns(thisType)
            .addParameter(int.class, "fromIndex")
            .addParameter(int.class, "toIndex")
            .addStatement("$T.fromToIndex(fromIndex, toIndex, size())", CHECK_CLASS)
            .addStatement("return new $L(array, this.fromIndex + fromIndex, this.fromIndex + toIndex)", className)
            .build());

        // compareTo method
        builder.addMethod(override("compareTo")
            .returns(int.class)
            .addParameter(thisType, "o")
            .addStatement("return $T.compare(array, fromIndex, toIndex, o.array, o.fromIndex, o.toIndex)", Arrays.class)
            .build());

        // equals method
        builder.addMethod(override("equals")
            .returns(boolean.class)
            .addParameter(Object.class, "obj")
            .addStatement("return obj instanceof $L o && $T.equals(array, fromIndex, toIndex, o.array, o.fromIndex, o.toIndex)", className, Arrays.class)
            .build());

        // hashCode method
        builder.addMethod(override("hashCode")
            .returns(int.class)
            .addStatement("return $T.hashCode(array, fromIndex, toIndex)", ARRAY_UTILS_CLASS)
            .build());

        // toString method
        builder.addMethod(override("toString")
            .returns(String.class)
            .addStatement("return $T.toString(array, fromIndex, toIndex)", ARRAY_UTILS_CLASS)
            .build());
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

        builder.addMethod(override("set")
            .returns(wrapperTypeName)
            .addParameter(int.class, "index")
            .addParameter(wrapperTypeName, "element")
            .addStatement("$L oldValue = get$L(index)", primitiveClass, bufferMethodName)
            .addStatement("set$L(index, element)", bufferMethodName)
            .addStatement("return oldValue")
            .build());

        return builder.build();
    }

    private static MethodSpec.Builder override(String name) {
        return MethodSpec.methodBuilder(name)
            .addModifiers(Modifier.PUBLIC)
            .addAnnotation(Override.class);
    }

    private static void writeClass(TypeSpec typeSpec) throws IOException {
        JavaFile
            .builder("be.twofold.valen.core.util.collect", typeSpec)
            .indent("    ")
            .build()
            .writeTo(Path.of("valen-core/src/main/java"));
    }
}
