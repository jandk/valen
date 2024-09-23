package org.redeye.valen.game.spacemarines2.types;

import java.util.*;

public record SerializedResource(ResourceHeader header, Map<String, ?> data) {
}
