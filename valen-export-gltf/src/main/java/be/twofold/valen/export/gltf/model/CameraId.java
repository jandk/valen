package be.twofold.valen.export.gltf.model;

public final class CameraId extends AbstractId {
    private CameraId(int id) {
        super(id);
    }

    public static CameraId of(int id) {
        return new CameraId(id);
    }
}
