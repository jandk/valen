package be.twofold.valen.gltf.model;

public final class SceneId extends AbstractId {
    private SceneId(int id) {
        super(id);
    }

    public static SceneId of(int id) {
        return new SceneId(id);
    }
}
