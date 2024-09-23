package org.redeye.valen.game.spacemarines2.fio;


public abstract class FioPrimitiveSerializerImpl<T> implements FioPrimitiveSerializer<T> {
    private int flags;

    public FioPrimitiveSerializerImpl(int flags) {
        this.flags = flags;
    }

    public FioPrimitiveSerializerImpl() {
        this(0);
    }

    @Override
    public int flags() {
        return flags;
    }
}
