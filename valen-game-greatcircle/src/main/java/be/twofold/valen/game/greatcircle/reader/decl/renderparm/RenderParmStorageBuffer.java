package be.twofold.valen.game.greatcircle.reader.decl.renderparm;

import java.util.*;

public record RenderParmStorageBuffer(
    Set<BufferViewFlag> flags,
    String name
) {
}
