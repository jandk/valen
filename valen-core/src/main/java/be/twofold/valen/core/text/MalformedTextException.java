package be.twofold.valen.core.text;

/**
 * Thrown to indicate that a text input is malformed or violates expected structural or encoding
 * rules. This exception is typically used when processing textual data or streams, and an
 * unrecoverable issue is encountered, such as improperly paired surrogate characters.
 * <p>
 * This class is immutable and thread-safe.
 */
public final class MalformedTextException extends RuntimeException {
    /**
     * Constructs a new {@code MalformedTextException} with the specified detail message.
     *
     * @param message the detail message
     */
    public MalformedTextException(String message) {
        super(message);
    }

    /**
     * Constructs a new {@code MalformedTextException} with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause   the cause (which is saved for later retrieval by the {@link #getCause()} method)
     */
    public MalformedTextException(String message, Throwable cause) {
        super(message, cause);
    }
}
