package be.twofold.valen.core.texture.pipeline;

final class Splat implements Stage {
    private final int source;
    private final int[] targets;

    private Splat(int source, int[] targets) {
        this.source = source;
        this.targets = targets;
    }

    static Splat of(int source, int... targets) {
        return new Splat(source, targets.clone());
    }

    @Override
    public void process(float[] tile, int pixelCount) {
        for (int i = 0; i < pixelCount * 4; i += 4) {
            float v = tile[i + source];
            for (int target : targets) {
                tile[i + target] = v;
            }
        }
    }
}
