package org.redeye.valen.game.spacemarines2.serializers;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.util.*;
import org.redeye.valen.game.spacemarines2.fio.*;
import org.redeye.valen.game.spacemarines2.psSection.*;

import java.io.*;

public class CachedPsSerializer implements FioSerializer<PsSectionValue.PsSectionObject> {
    @Override
    public PsSectionValue.PsSectionObject load(DataSource source) throws IOException {
        Chunk chunk = Chunk.read(source);
        PsSectionValue.PsSectionObject parsed = null;
        while (!chunk.isTerminator()) {
            if (chunk.id() == 0) {
                var parser = new PsSectionParser(new StringReader(source.readPString()));
                parsed = parser.parse();
            } else if (chunk.id() == 1) {
                var value = source.readInt();
                Check.state(false, "Chunk 1 for CachedPsSerializer not implemented.");
            }

            if (chunk.endOffset() != source.tell()) {
                System.err.printf("CachedPsSerializer: Chunk(%d) under/over read. Expected %d, got %d.%n", chunk.id(), chunk.endOffset(), source.tell());
                source.seek(chunk.endOffset());
            }
            chunk = Chunk.read(source);
        }
        if (parsed != null) {
            return parsed;
        }
        return null;
    }

    @Override
    public int flags() {
        return 1;
    }
}