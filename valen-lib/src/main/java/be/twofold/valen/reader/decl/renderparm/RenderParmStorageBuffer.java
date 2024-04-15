package be.twofold.valen.reader.decl.renderparm;

import be.twofold.valen.reader.decl.renderparm.enums.*;

import java.util.*;

public record RenderParmStorageBuffer(
    Set<BufferViewFlag> flags,
    String name
) {
}
