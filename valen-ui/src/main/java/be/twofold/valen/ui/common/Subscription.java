package be.twofold.valen.ui.common;

public interface Subscription extends AutoCloseable {
    @Override
    void close();
}
