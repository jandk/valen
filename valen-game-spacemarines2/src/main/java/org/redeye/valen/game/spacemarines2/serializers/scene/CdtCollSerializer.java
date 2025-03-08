package org.redeye.valen.game.spacemarines2.serializers.scene;

import be.twofold.valen.core.io.*;
import org.redeye.valen.game.spacemarines2.fio.*;
import org.redeye.valen.game.spacemarines2.types.scene.*;

import java.io.*;

public class CdtCollSerializer implements FioSerializer<CdtColl> {
    @Override
    public CdtColl load(DataSource source) throws IOException {
        while (true) {
            Chunk chunk = Chunk.read(source);
            if (chunk.isTerminator()) {
                break;
            }

            if (chunk.endOffset() != source.position()) {
                System.err.printf("CdtCollSerializer: Under/over read of chunk. Expected %d, got %d%n%n", chunk.endOffset(), source.position());
                source.position(chunk.endOffset());
            }
        }
        return null;
    }

    @Override
    public int flags() {
        return 1;
    }
}
