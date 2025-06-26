package be.twofold.valen.core.util.collect;

import java.io.*;
import java.nio.file.*;

public class WrapperGenerator {
    private static final String TEMPLATE = """
        package be.twofold.valen.core.util.collect;
        
        import be.twofold.valen.core.util.*;
        
        import java.util.*;
        
        public class {class} extends AbstractList<{wrapper}> implements Comparable<{class}>, RandomAccess {
            final {primitive}[] array;
            final int fromIndex;
            final int toIndex;
        
            {class}({primitive}[] array, int fromIndex, int toIndex) {
                Check.fromToIndex(fromIndex, toIndex, array.length);
                this.array = array;
                this.fromIndex = fromIndex;
                this.toIndex = toIndex;
            }
        
            public static {class} wrap({primitive}[] array) {
                return new {class}(array, 0, array.length);
            }
        
            public static {class} wrap({primitive}[] array, int fromIndex, int toIndex) {
                return new {class}(array, fromIndex, toIndex);
            }
        
            public {primitive} get{primitiveUpper}(int index) {
                Check.index(index, size());
                return array[fromIndex + index];
            }
        
        
            @Override
            public int size() {
                return toIndex - fromIndex;
            }
        
            @Override
            @Deprecated
            public {wrapper} get(int index) {
                return get{primitiveUpper}(index);
            }
        
            @Override
            public boolean contains(Object o) {
                return o instanceof {wrapper} value
                       && ArrayUtils.contains(array, fromIndex, toIndex, value);
            }
        
            @Override
            public int indexOf(Object o) {
                if (o instanceof {wrapper} value) {
                    int index = ArrayUtils.indexOf(array, fromIndex, toIndex, value);
                    if (index >= 0) {
                        return index - fromIndex;
                    }
                }
                return -1;
            }
        
            @Override
            public int lastIndexOf(Object o) {
                if (o instanceof {wrapper} value) {
                    int index = ArrayUtils.lastIndexOf(array, fromIndex, toIndex, value);
                    if (index >= 0) {
                        return index - fromIndex;
                    }
                }
                return -1;
            }
        
            @Override
            public {class} subList(int fromIndex, int toIndex) {
                Check.fromToIndex(fromIndex, toIndex, size());
                return new {class}(array, this.fromIndex + fromIndex, this.fromIndex + toIndex);
            }
        
        
            @Override
            public int compareTo({class} o) {
                return Arrays.compare(array, fromIndex, toIndex, o.array, o.fromIndex, o.toIndex);
            }
        
            @Override
            public boolean equals(Object obj) {
                return obj instanceof {class} o
                       && Arrays.equals(array, fromIndex, toIndex, o.array, o.fromIndex, o.toIndex);
            }
        
            @Override
            public int hashCode() {
                return ArrayUtils.hashCode(array, fromIndex, toIndex);
            }
        
            @Override
            public String toString() {
                return ArrayUtils.toString(array, fromIndex, toIndex);
            }
        }
        """;

    private static final String MUTABLE_TEMPLATE = """
        package be.twofold.valen.core.util.collect;
        
        import be.twofold.valen.core.util.*;
        
        public final class Mutable{class} extends {class} {
            private Mutable{class}({primitive}[] array, int fromIndex, int toIndex) {
                super(array, fromIndex, toIndex);
            }
        
            public static Mutable{class} wrap({primitive}[] array) {
                return new Mutable{class}(array, 0, array.length);
            }
        
            public static Mutable{class} wrap({primitive}[] array, int fromIndex, int toIndex) {
                return new Mutable{class}(array, fromIndex, toIndex);
            }
        
            public void set{primitiveUpper}(int index, {primitive} value){
                Check.index(index, size());
                array[fromIndex + index] = value;
            }
        
            @Override
            public {wrapper} set(int index, {wrapper} element) {
                {primitive} oldValue = get{primitiveUpper}(index);
                set{primitiveUpper}(index, element);
                return oldValue;
            }
        }
        """;

    public static void main(String[] args) throws IOException {
        generate("Bytes", "Byte", "byte", "Byte");
        generate("Shorts", "Short", "short", "Short");
        generate("Ints", "Integer", "int", "Int");
        generate("Longs", "Long", "long", "Long");
        generate("Floats", "Float", "float", "Float");
        generate("Doubles", "Double", "double", "Double");
    }

    private static void generate(String className, String wrapperName, String primitiveName, String primitiveUpperName) throws IOException {
        String code = TEMPLATE
            .replace("{class}", className)
            .replace("{wrapper}", wrapperName)
            .replace("{primitive}", primitiveName)
            .replace("{primitiveUpper}", primitiveUpperName);
        Files.writeString(Path.of("valen-core/src/main/java/be/twofold/valen/core/util/collect/" + className + ".java"), code);

        String mutableCode = MUTABLE_TEMPLATE
            .replace("{class}", className)
            .replace("{wrapper}", wrapperName)
            .replace("{primitive}", primitiveName)
            .replace("{primitiveUpper}", primitiveUpperName);
        Files.writeString(Path.of("valen-core/src/main/java/be/twofold/valen/core/util/collect/Mutable" + className + ".java"), mutableCode);
    }
}
