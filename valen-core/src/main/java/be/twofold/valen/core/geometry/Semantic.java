package be.twofold.valen.core.geometry;

public sealed interface Semantic {
    Semantic Position = new Position();
    Semantic Normal = new Normal();
    Semantic Tangent = new Tangent();
    Semantic TexCoord0 = new TexCoord(0);
    Semantic TexCoord1 = new TexCoord(1);
    Semantic Color0 = new Color(0);
    Semantic Color1 = new Color(1);
    Semantic Joints0 = new Joints(0);
    Semantic Joints1 = new Joints(1);
    Semantic Weights0 = new Weights(0);
    Semantic Weights1 = new Weights(1);

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
