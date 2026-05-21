package be.twofold.valen.core.texture.shader;

import java.util.*;
import java.util.concurrent.*;

final class TilePool {
    private final int bufferSize;
    private final Deque<float[]> pool = new ConcurrentLinkedDeque<>();

    TilePool(int tileSize) {
        this.bufferSize = tileSize * tileSize * 4;
    }

    float[] acquire() {
        var buf = pool.pollFirst();
        return buf != null ? buf : new float[bufferSize];
    }

    void release(float[] buf) {
        pool.addFirst(buf);
    }
}
