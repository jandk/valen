package be.twofold.valen.core.geometry;

import wtf.reversed.toolbox.util.*;

/**
 * Identity of a vertex attribute, what the attribute is, not how it is stored.
 * <p>
 * Used as the key of a mesh attribute map, so instances are value types with record equality
 */
public sealed interface Semantic {
    Position POSITION = new Position();
    Normal NORMAL = new Normal();
    Tangent TANGENT = new Tangent();
    Joints JOINTS = new Joints();
    Weights WEIGHTS = new Weights();

    record Position() implements Semantic {
    }

    record Normal() implements Semantic {
    }

    record Tangent() implements Semantic {
    }

    record TexCoord(int set) implements Semantic {
        public TexCoord {
            Check.positiveOrZero(set, "set");
        }
    }

    record Color(int set) implements Semantic {
        public Color {
            Check.positiveOrZero(set, "set");
        }
    }

    record Joints() implements Semantic {
    }

    record Weights() implements Semantic {
    }

    record Custom(String name) implements Semantic {
        public Custom {
            Check.nonNull(name, "name");
        }
    }
}
