package be.twofold.valen.core.util;

@FunctionalInterface
public interface ThrowingSupplier<T, X extends Exception> {
    T get() throws X;

    static <T, X extends Exception> ThrowingSupplier<T, X> lazy(
        ThrowingSupplier<T, X> supplier
    ) {
        Check.nonNull(supplier, "supplier");

        return new ThrowingSupplier<>() {
            private volatile ThrowingSupplier<T, X> delegate = supplier;
            private T value;

            @Override
            public T get() throws X {
                if (delegate != null) {
                    synchronized (this) {
                        if (delegate != null) {
                            T value = delegate.get();
                            this.value = value;
                            delegate = null;
                            return value;
                        }
                    }
                }
                return value;
            }
        };
    }
}
