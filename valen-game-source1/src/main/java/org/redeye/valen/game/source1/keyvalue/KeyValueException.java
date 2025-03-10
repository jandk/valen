package org.redeye.valen.game.source1.keyvalue;

public class KeyValueException extends RuntimeException {
    public KeyValueException(String message) {
        super(message);
    }

    public KeyValueException(String message, Throwable cause) {
        super(message, cause);
    }

    public KeyValueException(Throwable cause) {
        super(cause);
    }
}
