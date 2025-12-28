package be.twofold.valen.core.util;

import org.junit.jupiter.api.*;

import java.lang.reflect.*;
import java.util.*;

import static org.assertj.core.api.Assertions.*;

public class ParameterizedTypeTest {
    interface GenericInterface<T> {
    }

    interface AnotherInterface<K, V> {
    }

    static class BaseClass<T> {
    }

    static class MidClass<E> extends BaseClass<String> implements GenericInterface<List<E>> {
    }

    static class ConcreteClass extends MidClass<Integer> implements AnotherInterface<Double, Boolean> {
    }

    @Test
    void testFindBaseClass() {
        var actual = Reflections.getParameterizedType(ConcreteClass.class, BaseClass.class);
        assertThat(actual).hasValueSatisfying(pt -> {
            assertThat(pt.getRawType()).isEqualTo(BaseClass.class);
            assertThat(pt.getActualTypeArguments()).containsExactly(String.class);
        });
    }

    @Test
    void testFindGenericInterface() {
        var actual = Reflections.getParameterizedType(ConcreteClass.class, GenericInterface.class);
        assertThat(actual).hasValueSatisfying(pt1 -> {
            assertThat(pt1.getRawType()).isEqualTo(GenericInterface.class);
            assertThat(pt1.getActualTypeArguments()).hasOnlyOneElementSatisfying(type -> {
                assertThat(type).isInstanceOfSatisfying(ParameterizedType.class, pt2 -> {
                    assertThat(pt2.getRawType()).isEqualTo(List.class);
                    assertThat(pt2.getActualTypeArguments()).hasOnlyOneElementSatisfying(t -> {
                        assertThat(t).isInstanceOf(TypeVariable.class);
                    });
                });
            });
        });
    }

    @Test
    void testFindAnotherInterface() {
        var actual = Reflections.getParameterizedType(ConcreteClass.class, AnotherInterface.class);
        assertThat(actual).hasValueSatisfying(pt -> {
            assertThat(pt.getRawType()).isEqualTo(AnotherInterface.class);
            assertThat(pt.getActualTypeArguments()).containsExactly(Double.class, Boolean.class);
        });
    }

    @Test
    void testFindNonExistingType() {
        var actual = Reflections.getParameterizedType(ConcreteClass.class, Map.class);
        assertThat(actual).isEmpty();
    }
}
