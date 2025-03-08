package be.twofold.valen.ui.common.event;

@FunctionalInterface
public interface SendChannel<E> {
    void send(E event);
}
