package be.twofold.valen.ui.event;

@FunctionalInterface
public interface SendChannel<E> {
    void send(E event);
}
