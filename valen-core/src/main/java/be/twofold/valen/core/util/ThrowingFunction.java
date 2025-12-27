package be.twofold.valen.core.util;

@FunctionalInterface
public interface ThrowingFunction<T, R, X extends Exception> {
    R apply(T t) throws X;
}
