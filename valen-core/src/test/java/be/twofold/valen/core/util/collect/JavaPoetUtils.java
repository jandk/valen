package be.twofold.valen.core.util.collect;

import com.squareup.javapoet.*;

import javax.lang.model.element.*;
import java.util.function.*;

public final class JavaPoetUtils {
    private static final ClassName COMPARABLE = ClassName.get(Comparable.class);

    private JavaPoetUtils() {
    }

    public static void implementComparable(
        TypeSpec.Builder builder,
        ClassName className,
        Consumer<MethodSpec.Builder> methodBuilderConsumer
    ) {
        var methodBuilder = override("compareTo")
            .returns(int.class)
            .addParameter(className, "o");
        methodBuilderConsumer.accept(methodBuilder);

        builder
            .addSuperinterface(ParameterizedTypeName.get(COMPARABLE, className))
            .addMethod(methodBuilder.build());
    }

    public static CodeBlock primitiveEquals(String left, String right, Class<?> type) {
        if (type == byte.class || type == short.class || type == int.class || type == long.class) {
            return CodeBlock.of("$L == $L", left, right);
        } else if (type == float.class) {
            return CodeBlock.of("$T.compare($L, $L) == 0", Float.class, left, right);
        } else if (type == double.class) {
            return CodeBlock.of("$T.compare($L, $L) == 0", Double.class, left, right);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    public static MethodSpec.Builder override(String name) {
        return MethodSpec.methodBuilder(name)
            .addModifiers(Modifier.PUBLIC)
            .addAnnotation(Override.class);
    }

    public static MethodSpec.Builder equalsBuilder(String parameterName) {
        return override("equals")
            .returns(boolean.class)
            .addParameter(Object.class, parameterName);
    }

    public static MethodSpec.Builder hashCodeBuilder() {
        return override("hashCode")
            .returns(int.class);
    }

    public static MethodSpec.Builder toStringBuilder() {
        return override("toString")
            .returns(String.class);
    }
}
