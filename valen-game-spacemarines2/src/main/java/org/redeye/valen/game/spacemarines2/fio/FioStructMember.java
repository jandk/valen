package org.redeye.valen.game.spacemarines2.fio;

import org.redeye.valen.game.spacemarines2.serializers.*;

import java.util.function.*;

public record FioStructMember<T, V>(String name, BiConsumer<T, V> setter, FioSerializer<V> deserializer) {
    private static final FioStructMember<?, Void> NULL_MEMBER = new FioStructMember<>("null", FioStructMember::nullSetter, new NullSerializer());

    @SuppressWarnings("unchecked")
    public static <T> FioStructMember<T, Void> nullMember() {
        return (FioStructMember<T, Void>) NULL_MEMBER;
    }

    private static <T, V> void nullSetter(T object, V value) {
        // do nothing
    }
}

