package be.twofold.valen.format.granite.xml;

import java.time.*;
import java.util.*;

public record XmlTexture(
    String src,
    Optional<Integer> row,
    Optional<Integer> column,
    int subIndex,
    int width,
    int height,
    int arrayIndex,
    Instant lastChangeDate,
    int numChannels
) {
}
