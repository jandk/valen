package org.redeye.valen.game.spacemarines2.serializers;

import be.twofold.valen.core.io.*;
import org.redeye.valen.game.spacemarines2.fio.*;
import org.redeye.valen.game.spacemarines2.types.*;

import java.io.*;

public class SplineSerializer implements FioSerializer<Spline> {
    @Override
    public Spline load(DataSource source) throws IOException {
        var chunk = Chunk.read(source);
        while (chunk.id() != 1) {
            switch (chunk.id()) {
                default -> {
                }
            }
            if (chunk.endOffset() != source.tell()) {
                System.err.printf("Under/over read of spline chunk. Expected %d, got %d%n%n", chunk.endOffset(), source.tell());
                source.seek(chunk.endOffset());
            }
            chunk = Chunk.read(source);
        }

        return new Spline();
    }

    @Override
    public int flags() {
        return 0;
    }
}
