package be.twofold.valen.core.util.fi;

import be.twofold.valen.core.util.*;

final class LazyThrowingSupplier<T, X extends Exception> implements ThrowingSupplier<T, X> {
    private volatile ThrowingSupplier<T, X> delegate;
    private T value;

    LazyThrowingSupplier(ThrowingSupplier<T, X> delegate) {
        this.delegate = Check.notNull(delegate);
    }

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
}
