package be.twofold.valen.core.util;

import wtf.reversed.toolbox.math.*;

import java.lang.invoke.*;
import java.lang.reflect.*;
import java.util.*;

public interface Meta {

    static Node build(MethodHandles.Lookup lookup, Object value) {
        if (value == null || value instanceof Primitive) {
            return new Value(value);
        }

        var clazz = value.getClass();
        if (clazz.isRecord()) {
            var fields = Arrays.stream(clazz.getRecordComponents())
                .map(component -> new Item(component.getName(), build(lookup, invoke(lookup, component, value))))
                .toList();
            return new Struct(clazz.getSimpleName(), List.copyOf(fields));
        }
        if (value instanceof Collection<?> collection) {
            var items = collection.stream()
                .map(item -> build(lookup, item))
                .toList();
            return new Sequence(items);
        }
        return new Value(value);
    }

    static Object invoke(MethodHandles.Lookup lookup, RecordComponent component, Object target) {
        try {
            return lookup.unreflect(component.getAccessor()).invoke(target);
        } catch (IllegalAccessException e) {
            try {
                return MethodHandles.publicLookup().unreflect(component.getAccessor()).invoke(target);
            } catch (Throwable t) {
                return "!ACCESS!";
            }
        } catch (Throwable t) {
            return "!ERROR!";
        }
    }

    record Item(
        String name,
        Node node
    ) {
        public Item {
            Objects.requireNonNull(name);
            Objects.requireNonNull(node);
        }
    }

    sealed interface Node permits Struct, Sequence, Value {
    }

    record Struct(
        String typeName,
        List<Item> fields
    ) implements Node {
        public Struct {
            Objects.requireNonNull(typeName);
            fields = List.copyOf(fields);
        }
    }

    record Sequence(List<Node> items) implements Node {
        public Sequence {
            items = List.copyOf(items);
        }
    }

    record Value(Object raw) implements Node {
    }

}
