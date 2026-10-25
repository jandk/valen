package be.twofold.valen.core.texture.shader;

public final class TileBuffer {
    private final TilePool pool;
    private final float[] data;
    private int refCount;

    TileBuffer(TilePool pool, int refCount) {
        this.pool = pool;
        this.data = pool.acquire();
        this.refCount = refCount;
    }

    public float[] data() {
        return data;
    }

    public boolean isLast() {
        return refCount == 1;
    }

    public void resetRefCount(int refCount) {
        this.refCount = refCount;
    }

    public void release() {
        if (--refCount == 0) {
            pool.release(data);
        }
    }
}
