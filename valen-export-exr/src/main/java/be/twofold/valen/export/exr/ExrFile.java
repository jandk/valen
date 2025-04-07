package be.twofold.valen.export.exr;

import be.twofold.valen.core.util.*;

import java.util.*;

record ExrFile(ExrHeader header, List<Long> offsetTable, List<ExrChunk> chunks) {
    public ExrFile {
        Check.notNull(header, "header");
        offsetTable = List.copyOf(offsetTable);
        chunks = List.copyOf(chunks);
    }
}
