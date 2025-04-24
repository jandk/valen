package be.twofold.valen.game.greatcircle.reader.geometry;

import java.util.*;

public interface GeoDiskLayout {

    int compression();

    int uncompressedSize();

    int compressedSize();

    int offset();

    List<? extends GeoMemoryLayout> memoryLayouts();

}
