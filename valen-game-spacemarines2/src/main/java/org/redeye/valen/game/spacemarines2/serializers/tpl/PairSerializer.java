package org.redeye.valen.game.spacemarines2.serializers.tpl;

import be.twofold.valen.core.io.*;
import org.redeye.valen.game.spacemarines2.fio.*;
import org.redeye.valen.game.spacemarines2.types.*;

import java.io.*;

public class PairSerializer<K, V> implements FioSerializer<Pair<K, V>> {
    protected FioSerializer<K> firstSerializer;
    protected FioSerializer<V> secondSerializer;

    public PairSerializer(FioSerializer<K> firstSerializer, FioSerializer<V> secondSerializer) {
        this.firstSerializer = firstSerializer;
        this.secondSerializer = secondSerializer;
    }

    @Override
    public Pair<K, V> load(DataSource source) throws IOException {
        var first = firstSerializer.load(source);
        var second = secondSerializer.load(source);
        return new Pair<>(first, second);
    }

    @Override
    public int flags() {
        return 0;
    }
}
