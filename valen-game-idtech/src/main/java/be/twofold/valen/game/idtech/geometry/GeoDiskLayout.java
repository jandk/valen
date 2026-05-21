package be.twofold.valen.game.idtech.geometry;

import java.util.*;

public interface GeoDiskLayout {

    int compression();

    int uncompressedSize();

    int compressedSize();

    int offset();

    List<? extends GeoMemoryLayout> memoryLayouts();

}
