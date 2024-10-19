package org.redeye.valen.game.spacemarines2.fio;


public abstract class FioPrimitiveSerializerImpl<T> implements FioPrimitiveSerializer<T> {
    @Override
    public final int flags() {
        return 16;
    }
}
