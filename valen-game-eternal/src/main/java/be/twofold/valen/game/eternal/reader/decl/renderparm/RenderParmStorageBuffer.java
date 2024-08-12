package be.twofold.valen.game.eternal.reader.decl.renderparm;

import be.twofold.valen.game.eternal.reader.decl.renderparm.enums.*;

import java.util.*;

public record RenderParmStorageBuffer(
    Set<BufferViewFlag> flags,
    String name
) {
}
