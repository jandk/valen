package org.redeye.valen.game.spacemarines2.serializers.template;

import be.twofold.valen.core.io.*;
import org.redeye.valen.game.spacemarines2.fio.*;
import org.redeye.valen.game.spacemarines2.types.template.*;

import java.io.*;
import java.util.*;

public class TxmTexListSerializer implements FioSerializer<TxmTex> {
    @Override
    public TxmTex load(DataSource source) throws IOException {
        var texList = new TxmTex();
        var textureCount = source.readInt();
        var refs = new ArrayList<String>(textureCount);
        Chunk chunk = Chunk.read(source);
        while (!chunk.isTerminator()) {
            if (chunk.id() == 0 && textureCount > 0) {
                for (int i = 0; i < textureCount; i++) {
                    refs.add(source.readString(source.readShort()));
                }
            }
            if (chunk.endOffset() != source.tell()) {
                System.err.printf("TxmTexListSerializer: Under/over read of chunk. Expected %d, got %d%n%n", chunk.endOffset(), source.tell());
                source.seek(chunk.endOffset());
            }
            chunk = Chunk.read(source);
        }
        texList.setTextreRefs(refs);
        return texList;
    }

    @Override
    public int flags() {
        return 0;
    }
}
