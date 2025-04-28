package be.twofold.valen.game.eternal.reader.decl.renderparm;

import be.twofold.valen.game.eternal.defines.BufferViewFlag;

import java.util.Set;

public record RenderParmStorageBuffer(
    Set<BufferViewFlag> flags,
    String name
) {
}
