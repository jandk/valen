package be.twofold.valen.game.idtech.renderparm;

import be.twofold.valen.game.idtech.defines.*;

import java.util.*;

public record RenderParmStorageBuffer(
    Set<BufferViewFlag> flags,
    String name
) {
}
