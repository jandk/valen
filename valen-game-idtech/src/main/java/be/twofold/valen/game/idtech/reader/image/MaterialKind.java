package be.twofold.valen.game.idtech.reader.image;

import wtf.reversed.toolbox.util.*;

public interface MaterialKind extends ValueEnum<Integer> {

    String suffix();

    boolean skipsFormat(TextureFormat format);

    boolean isSmoothness();

}
