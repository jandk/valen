package be.twofold.valen.core.util.fi;

@FunctionalInterface
public interface ThrowingSupplier<T, X extends Exception> {
    T get() throws X;

    static <T, X extends Exception> ThrowingSupplier<T, X> lazy(
        ThrowingSupplier<T, X> supplier
    ) {
        return new LazyThrowingSupplier<>(supplier);
    }
}
