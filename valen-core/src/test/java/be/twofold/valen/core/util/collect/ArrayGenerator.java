package be.twofold.valen.core.util.collect;

import com.squareup.javapoet.*;
import org.jetbrains.annotations.*;

import javax.lang.model.element.*;
import java.io.*;
import java.lang.invoke.*;
import java.nio.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

final class ArrayGenerator {
    private static final String PACKAGE_NAME = "be.twofold.valen.core.util.collect";
    private static final ClassName PARENT_CLASS = ClassName.get(PACKAGE_NAME, "Array");
    private static final ClassName CHECK_CLASS = ClassName.get("be.twofold.valen.core.util", "Check");

    private final ClassName thisType;
    private final ClassName mutableType;
    private final Class<?> primitiveType;
    private final TypeName arrayType;
    private final ClassName wrapperType;
    private final ClassName bufferType;
    private TypeSpec.Builder builder;

    ArrayGenerator(String className, Class<?> primitiveType, Class<?> wrapperType, Class<?> bufferType) {
        this.thisType = ClassName.get("", className);
        this.mutableType = ClassName.get("", "Mutable" + className);
        this.primitiveType = primitiveType;
        this.arrayType = ArrayTypeName.of(TypeName.get(primitiveType));
        this.wrapperType = ClassName.get(wrapperType);
        this.bufferType = ClassName.get(bufferType);
    }

    public static void main(String[] args) throws IOException {
        generateParent();
        new ArrayGenerator("Bytes", byte.class, Byte.class, ByteBuffer.class).generate();
        new ArrayGenerator("Shorts", short.class, Short.class, ShortBuffer.class).generate();
        new ArrayGenerator("Ints", int.class, Integer.class, IntBuffer.class).generate();
        new ArrayGenerator("Longs", long.class, Long.class, LongBuffer.class).generate();
        new ArrayGenerator("Floats", float.class, Float.class, FloatBuffer.class).generate();
        new ArrayGenerator("Doubles", double.class, Double.class, DoubleBuffer.class).generate();
    }

    private static void generateParent() throws IOException {
        writeClass(createInterface());
    }

    private void generate() throws IOException {
        writeClass(createWrapperClass());
        writeClass(createMutableWrapperClass());
    }

    private static @NotNull TypeSpec createInterface() {
        return TypeSpec.interfaceBuilder(PARENT_CLASS)
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
    }

    private TypeSpec createWrapperClass() {
        this.builder = TypeSpec.classBuilder(thisType)
            .addModifiers(Modifier.PUBLIC)
            .addSuperinterface(PARENT_CLASS)
            .addAnnotation(AnnotationSpec.builder(Debug.Renderer.class)
                .addMember("childrenArray", "$S", "java.util.Arrays.copyOfRange(array, offset, offset + length)")
                .build());

        // Fields
        builder.addField(FieldSpec.builder(thisType, "EMPTY", Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
            .initializer("wrap(new $T[0])", primitiveType)
            .build());
        if (primitiveType == byte.class) {
            for (Class<?> type : List.of(short.class, int.class, long.class, float.class, double.class)) {
                builder.addField(FieldSpec.builder(VarHandle.class, varHandleName(type), Modifier.STATIC, Modifier.FINAL)
                    .initializer("$T.byteArrayViewVarHandle($T[].class, $T.$L).withInvokeExactBehavior()", MethodHandles.class, type, ByteOrder.class, ByteOrder.LITTLE_ENDIAN)
                    .build());
            }
        }

        builder.addField(FieldSpec.builder(arrayType, "array", Modifier.FINAL).build());
        builder.addField(FieldSpec.builder(int.class, "offset", Modifier.FINAL).build());
        builder.addField(FieldSpec.builder(int.class, "length", Modifier.FINAL).build());

        addConstructors();
        addFactories();
        addGetters();
        addListMethods();
        addSliceMethods(thisType);
        addBulkMethods();
        addConversions();
        addComparableMethods();
        addObjectMethods();

        return builder.build();
    }

    private void addConstructors() {
        builder.addMethod(MethodSpec.constructorBuilder()
            .addParameter(arrayType, "array")
            .addParameter(int.class, "offset")
            .addParameter(int.class, "length")
            .addStatement("$T.fromIndexSize(offset, length, array.length)", CHECK_CLASS)
            .addStatement("this.array = array")
            .addStatement("this.offset = offset")
            .addStatement("this.length = length")
            .build());
    }

    private void addFactories() {
        builder.addMethod(MethodSpec.methodBuilder("empty")
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .returns(thisType)
            .addStatement("return EMPTY")
            .build());

        addWrapMethods(thisType);

        builder.addMethod(MethodSpec.methodBuilder("from")
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .addParameter(bufferType, "buffer")
            .returns(thisType)
            .addStatement("$T.argument(buffer.hasArray(), \"buffer must be backed by an array\")", CHECK_CLASS)
            .addStatement("return new $L(buffer.array(), buffer.position(), buffer.limit())", thisType)
            .build());
    }

    private void addGetters() {
        builder.addMethod(MethodSpec.methodBuilder("get")
            .addModifiers(Modifier.PUBLIC)
            .addParameter(int.class, "index")
            .returns(primitiveType)
            .addStatement("$T.index(index, length)", CHECK_CLASS)
            .addStatement("return array[offset + index]")
            .build());

        // Add extra methods for Primitives
        if (primitiveType == byte.class) {
            generateGet(short.class, "getShort", "Short.BYTES");
            generateGet(int.class, "getInt", "Integer.BYTES");
            generateGet(long.class, "getLong", "Long.BYTES");
            generateGet(float.class, "getFloat", "Float.BYTES");
            generateGet(double.class, "getDouble", "Double.BYTES");
            generateGetUnsigned(int.class, "getUnsigned", "get", "Byte.toUnsignedInt");
            generateGetUnsigned(int.class, "getUnsignedShort", "getShort", "Short.toUnsignedInt");
            generateGetUnsigned(long.class, "getUnsignedInt", "getInt", "Integer.toUnsignedLong");
        } else if (primitiveType == short.class) {
            generateGetUnsigned(int.class, "getUnsigned", "get", "Short.toUnsignedInt");
        } else if (primitiveType == int.class) {
            generateGetUnsigned(long.class, "getUnsigned", "get", "Integer.toUnsignedLong");
        }
    }

    private void addListMethods() {
        // length method
        builder.addMethod(JavaPoetUtils.override("length")
            .returns(int.class)
            .addStatement("return length")
            .build());

        // contains method
        builder.addMethod(MethodSpec.methodBuilder("contains")
            .addModifiers(Modifier.PUBLIC)
            .addParameter(primitiveType, "value")
            .returns(boolean.class)
            .addStatement("return indexOf(value) >= 0")
            .build());

        // indexOf method
        builder.addMethod(MethodSpec.methodBuilder("indexOf")
            .addModifiers(Modifier.PUBLIC)
            .addParameter(primitiveType, "value")
            .returns(int.class)
            .beginControlFlow("for (int i = offset, limit = offset + length; i < limit; i++)")
            .beginControlFlow("if (" + JavaPoetUtils.primitiveEquals("array[i]", "value", primitiveType) + ")")
            .addStatement("return i - offset")
            .endControlFlow()
            .endControlFlow()
            .addStatement("return -1")
            .build());

        // lastIndexOf method
        builder.addMethod(MethodSpec.methodBuilder("lastIndexOf")
            .addModifiers(Modifier.PUBLIC)
            .addParameter(primitiveType, "value")
            .returns(int.class)
            .beginControlFlow("for (int i = offset + length - 1; i >= offset; i--)")
            .beginControlFlow("if (" + JavaPoetUtils.primitiveEquals("array[i]", "value", primitiveType) + ")")
            .addStatement("return i - offset")
            .endControlFlow()
            .endControlFlow()
            .addStatement("return -1")
            .build());
    }

    private void addBulkMethods() {
        builder.addMethod(MethodSpec.methodBuilder("copyTo")
            .addModifiers(Modifier.PUBLIC)
            .addParameter(mutableType, "target")
            .addParameter(int.class, "offset")
            .returns(void.class)
            .addStatement("$T.arraycopy(array, this.offset, target.array, target.offset + offset, length)", System.class)
            .build());
    }

    private void addConversions() {
        builder.addMethod(JavaPoetUtils.override("asBuffer")
            .returns(bufferType)
            .addStatement("return $T.wrap(array, offset, length).asReadOnlyBuffer()", bufferType)
            .build());

        if (primitiveType == byte.class) {
            builder.addMethod(MethodSpec.methodBuilder("asInputStream")
                .addModifiers(Modifier.PUBLIC)
                .returns(InputStream.class)
                .addStatement("return new $T(array, offset, length)", ByteArrayInputStream.class)
                .build());
        }

        builder.addMethod(MethodSpec.methodBuilder("toArray")
            .addModifiers(Modifier.PUBLIC)
            .returns(arrayType)
            .addStatement("return $T.copyOfRange(array, offset, offset + length)", Arrays.class)
            .build());

        if (primitiveType == byte.class) {
            builder.addMethod(MethodSpec.methodBuilder("toHexString")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(HexFormat.class, "format")
                .returns(String.class)
                .addStatement("return format.formatHex(array, offset, offset + length)")
                .build());

            builder.addMethod(MethodSpec.methodBuilder("toString")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(Charset.class, "charset")
                .returns(String.class)
                .addStatement("return new String(array, offset, length, charset)")
                .build());
        }

        var returnType = switch (primitiveType.toString()) {
            case "byte", "short", "int" -> IntStream.class;
            case "long" -> LongStream.class;
            case "float", "double" -> DoubleStream.class;
            default -> throw new UnsupportedOperationException();
        };
        var statement = primitiveType == int.class || primitiveType == long.class || primitiveType == double.class
            ? CodeBlock.of("return $T.stream(array, offset, offset + length)", Arrays.class)
            : CodeBlock.of("return $T.range(offset, offset + length).map$L(i -> array[i])", IntStream.class, primitiveType == float.class ? "ToDouble" : "");
        builder.addMethod(MethodSpec.methodBuilder("stream")
            .addModifiers(Modifier.PUBLIC)
            .returns(returnType)
            .addStatement(statement)
            .build());
    }

    private void addComparableMethods() {
        JavaPoetUtils.implementComparable(builder, thisType, methodBuilder -> {
            methodBuilder.addStatement("return $T.compare(array, offset, offset + length, o.array, o.offset, o.offset + o.length)", Arrays.class);
        });
    }

    private void addObjectMethods() {
        builder.addMethod(JavaPoetUtils.equalsBuilder("obj")
            .addStatement("return obj instanceof $L o && $T.equals(array, offset, offset + length, o.array, o.offset, o.offset + o.length)", thisType, Arrays.class)
            .build());

        builder.addMethod(JavaPoetUtils.hashCodeBuilder()
            .addStatement("int result = 1")
            .beginControlFlow("for (int i = offset, limit = offset + length; i < limit; i++)")
            .addStatement("result = 31 * result + $T.hashCode(array[i])", wrapperType)
            .endControlFlow()
            .addStatement("return result")
            .build());

//        builder.addMethod(JavaPoetUtils.toStringBuilder()
//            .beginControlFlow("if (length == 0)")
//            .addStatement("return \"[]\"")
//            .endControlFlow()
//            .addStatement("StringBuilder builder = new StringBuilder()")
//            .addStatement("builder.append('[').append(array[offset])")
//            .beginControlFlow("for (int i = offset + 1, limit = offset + length; i < limit; i++)")
//            .addStatement("builder.append(\", \").append(array[i])")
//            .endControlFlow()
//            .addStatement("return builder.append(']').toString()")
//            .build());

        builder.addMethod(JavaPoetUtils.toStringBuilder()
            .addStatement("return $S + $L + $S", "[", "length", " " + primitiveType.toString() + "s]")
            .build());
    }

    private void generateGet(Class<?> returnType, String name, String length) {
        builder.addMethod(MethodSpec.methodBuilder(name)
            .addModifiers(Modifier.PUBLIC)
            .addParameter(int.class, "offset")
            .returns(returnType)
            .addStatement("$T.fromIndexSize(offset, $L, length)", CHECK_CLASS, length)
            .addStatement("return ($T) $L.get(array, this.offset + offset)", returnType, varHandleName(returnType))
            .build());
    }

    private void generateGetUnsigned(Class<?> returnType, String name, String accessor, String converter) {
        builder.addMethod(MethodSpec.methodBuilder(name)
            .addModifiers(Modifier.PUBLIC)
            .addParameter(int.class, "offset")
            .returns(returnType)
            .addStatement("return $L($L(offset))", converter, accessor)
            .build());
    }


    private TypeSpec createMutableWrapperClass() {
        builder = TypeSpec.classBuilder(mutableType)
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
            .superclass(thisType);

        addMutableConstructors();
        addMutableFactories();
        addMutableSetters();
        addSliceMethods(mutableType);
        addMutableBulkMethods();
        addMutableConversions();

        return builder.build();
    }

    private void addMutableConstructors() {
        builder.addMethod(MethodSpec.constructorBuilder()
            .addModifiers(Modifier.PRIVATE)
            .addParameter(arrayType, "array")
            .addParameter(int.class, "offset")
            .addParameter(int.class, "length")
            .addStatement("super(array, offset, length)")
            .build());
    }

    private void addMutableFactories() {
        addWrapMethods(mutableType);

        builder.addMethod(MethodSpec.methodBuilder("allocate")
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .addParameter(int.class, "length")
            .returns(mutableType)
            .addStatement("return new $L(new $L[length], 0, length)", mutableType, primitiveType)
            .build());
    }

    private void addMutableSetters() {
        builder.addMethod(MethodSpec.methodBuilder("set")
            .addModifiers(Modifier.PUBLIC)
            .addParameter(int.class, "index")
            .addParameter(primitiveType, "value")
            .returns(mutableType)
            .addStatement("$T.index(index, length)", CHECK_CLASS)
            .addStatement("array[offset + index] = value")
            .addStatement("return this")
            .build());

        if (primitiveType == byte.class) {
            generateSet(short.class, "setShort", "Short.BYTES");
            generateSet(int.class, "setInt", "Integer.BYTES");
            generateSet(long.class, "setLong", "Long.BYTES");
            generateSet(float.class, "setFloat", "Float.BYTES");
            generateSet(double.class, "setDouble", "Double.BYTES");
        }
    }

    private void addMutableBulkMethods() {
        builder.addMethod(MethodSpec.methodBuilder("fill")
            .addModifiers(Modifier.PUBLIC)
            .addParameter(primitiveType, "value")
            .returns(mutableType)
            .addStatement("$T.fill(array, offset, offset + length, value)", Arrays.class)
            .addStatement("return this")
            .build());
    }

    private void addMutableConversions() {
        builder.addMethod(MethodSpec.methodBuilder("asMutableBuffer")
            .addModifiers(Modifier.PUBLIC)
            .returns(bufferType)
            .addStatement("return $T.wrap(array, offset, length)", bufferType)
            .build());
    }

    private void generateSet(Class<?> valueType, String name, String length) {
        builder.addMethod(MethodSpec.methodBuilder(name)
            .addModifiers(Modifier.PUBLIC)
            .addParameter(int.class, "offset")
            .addParameter(valueType, "value")
            .returns(mutableType)
            .addStatement("$T.fromIndexSize(offset, $L, length)", CHECK_CLASS, length)
            .addStatement("$L.set(array, this.offset + offset, value)", varHandleName(valueType))
            .addStatement("return this")
            .build());
    }


    private void addSliceMethods(ClassName className) {
        builder.addMethod(MethodSpec.methodBuilder("slice")
            .addModifiers(Modifier.PUBLIC)
            .addParameter(int.class, "offset")
            .returns(className)
            .addStatement("return slice(offset, length - offset)")
            .build());

        builder.addMethod(MethodSpec.methodBuilder("slice")
            .addModifiers(Modifier.PUBLIC)
            .addParameter(int.class, "offset")
            .addParameter(int.class, "length")
            .returns(className)
            .addStatement("$T.fromIndexSize(offset, length, this.length)", CHECK_CLASS)
            .addStatement("return new $L(array, this.offset + offset, length)", className)
            .build());
    }

    private void addWrapMethods(ClassName className) {
        builder.addMethod(MethodSpec.methodBuilder("wrap")
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .addParameter(arrayType, "array")
            .returns(className)
            .addStatement("return new $L(array, 0, array.length)", className)
            .build());

        builder.addMethod(MethodSpec.methodBuilder("wrap")
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .addParameter(arrayType, "array")
            .addParameter(int.class, "offset")
            .addParameter(int.class, "length")
            .returns(className)
            .addStatement("return new $L(array, offset, length)", className)
            .build());
    }

    private String varHandleName(Class<?> type) {
        return "VH_" + type.getSimpleName().toUpperCase() + "_LE";
    }

    private static void writeClass(TypeSpec typeSpec) throws IOException {
        JavaFile
            .builder(PACKAGE_NAME, typeSpec)
            .indent("    ")
            .build()
            .writeTo(Path.of("valen-core/src/main/java"));
    }
}
