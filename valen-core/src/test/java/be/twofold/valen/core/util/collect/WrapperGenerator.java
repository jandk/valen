package be.twofold.valen.core.util.collect;

import java.io.*;
import java.nio.file.*;

public class WrapperGenerator {
    private static final String TEMPLATE = """
        package be.twofold.valen.core.util.collect;
        
        import be.twofold.valen.core.util.*;
        
        import java.nio.*;
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
        
            public static {class} allocate(int size) {
                Check.argument(size >= 0, "size must be non-negative");
                return new {class}(new {primitive}[size], 0, size);
            }
        
            public static {class} from({primitiveUpper}Buffer buffer) {
                Check.argument(buffer.hasArray(), "buffer must be backed by an array");
                return new {class}(buffer.array(), buffer.position(), buffer.limit());
            }
        
            public {primitive} get{primitiveUpper}(int index) {
                Check.index(index, size());
                return array[fromIndex + index];
            }
            {extraMethods}
            public {primitiveUpper}Buffer asBuffer() {
                return {primitiveUpper}Buffer.wrap(array, fromIndex, size()).asReadOnlyBuffer();
            }
        
            public void copyTo(Mutable{class} target, int offset) {
                System.arraycopy(array, fromIndex, target.array, target.fromIndex + offset, size());
            }
        
            public {class} slice(int fromIndex) {
                return subList(fromIndex, size());
            }
        
            public {class} slice(int fromIndex, int toIndex) {
                return subList(fromIndex, toIndex);
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
        
        import java.nio.*;
        
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
        
            public static Mutable{class} allocate(int size) {
                return new Mutable{class}(new {primitive}[size], 0, size);
            }
        
            public void set{primitiveUpper}(int index, {primitive} value){
                Check.index(index, size());
                array[fromIndex + index] = value;
            }
        
            public {primitiveUpper}Buffer asMutableBuffer() {
                return {primitiveUpper}Buffer.wrap(array, fromIndex, size());
            }
        
            @Override
            public {wrapper} set(int index, {wrapper} element) {
                {primitive} oldValue = get{primitiveUpper}(index);
                set{primitiveUpper}(index, element);
                return oldValue;
            }
        }
        """;

    private static final String ACCESS_TEMPLATE = """
            public {prim} get{upper}(int offset) {
                Check.fromIndexSize(offset, {size}, size());
                return ByteArrays.get{upper}(array, fromIndex + offset);
            }
        """;

    private static final String ACCESS_TEMPLATE_UNSIGNED = """
            public {prim} getUnsigned{upper}(int offset) {
                return {conv}(get{upper}(offset));
            }
        """;

    public static void main(String[] args) throws IOException {
        var extraMethods = generateExtraByteMethods();

        generate("Bytes", "Byte", "byte", "Byte", extraMethods);
        generate("Shorts", "Short", "short", "Short", "");
        generate("Ints", "Integer", "int", "Int", "");
        generate("Longs", "Long", "long", "Long", "");
        generate("Floats", "Float", "float", "Float", "");
        generate("Doubles", "Double", "double", "Double", "");
    }

    private static String generateExtraByteMethods() {
        var builder = new StringBuilder("\n");

        generateUnsignedAccess(builder, "int", "Byte", "Byte.toUnsignedInt");
        generateAccess(builder, "short", "Short", "Short.BYTES");
        generateUnsignedAccess(builder, "int", "Short", "Short.toUnsignedInt");
        generateAccess(builder, "int", "Int", "Integer.BYTES");
        generateUnsignedAccess(builder, "long", "Int", "Integer.toUnsignedLong");
        generateAccess(builder, "long", "Long", "Long.BYTES");
        generateAccess(builder, "float", "Float", "Float.BYTES");
        generateAccess(builder, "double", "Double", "Double.BYTES");

        return builder.toString();
    }

    private static void generateAccess(StringBuilder builder, String prim, String upper, String size) {
        String code = ACCESS_TEMPLATE
            .replace("{prim}", prim)
            .replace("{upper}", upper)
            .replace("{size}", size);

        builder.append(code).append('\n');
    }

    private static void generateUnsignedAccess(StringBuilder builder, String prim, String upper, String conv) {
        String code = ACCESS_TEMPLATE_UNSIGNED
            .replace("{prim}", prim)
            .replace("{upper}", upper)
            .replace("{conv}", conv);

        builder.append(code).append('\n');
    }

    private static void generate(String className, String wrapperName, String primitiveName, String primitiveUpperName, String extraMethods) throws IOException {
        String code = TEMPLATE
            .replace("{class}", className)
            .replace("{wrapper}", wrapperName)
            .replace("{primitive}", primitiveName)
            .replace("{primitiveUpper}", primitiveUpperName)
            .replace("{extraMethods}", extraMethods);
        Files.writeString(Path.of("valen-core/src/main/java/be/twofold/valen/core/util/collect/" + className + ".java"), code);

        String mutableCode = MUTABLE_TEMPLATE
            .replace("{class}", className)
            .replace("{wrapper}", wrapperName)
            .replace("{primitive}", primitiveName)
            .replace("{primitiveUpper}", primitiveUpperName);
        Files.writeString(Path.of("valen-core/src/main/java/be/twofold/valen/core/util/collect/Mutable" + className + ".java"), mutableCode);
    }
}
