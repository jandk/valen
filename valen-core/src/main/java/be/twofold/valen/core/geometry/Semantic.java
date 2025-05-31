package be.twofold.valen.core.geometry;

public sealed interface Semantic {
    Semantic POSITION = new Position();
    Semantic NORMAL = new Normal();
    Semantic TANGENT = new Tangent();
    Semantic TEX_COORD0 = new TexCoord(0);
    Semantic TEX_COORD1 = new TexCoord(1);
    Semantic COLOR0 = new Color(0);
    Semantic COLOR1 = new Color(1);
    Semantic JOINTS0 = new Joints(0);
    Semantic JOINTS1 = new Joints(1);
    Semantic WEIGHTS0 = new Weights(0);
    Semantic WEIGHTS1 = new Weights(1);

    default int n() {
        return 0;
    }

    record Position() implements Semantic {
    }

    record Normal() implements Semantic {
    }

    record Tangent() implements Semantic {
    }

    record TexCoord(int n) implements Semantic {
    }

    record Color(int n) implements Semantic {
    }

    record Joints(int n) implements Semantic {
    }

    record Weights(int n) implements Semantic {
    }
}
