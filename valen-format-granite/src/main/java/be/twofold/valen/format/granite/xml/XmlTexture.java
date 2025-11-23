package be.twofold.valen.format.granite.xml;

import java.nio.file.*;
import java.time.*;

public record XmlTexture(
    Path src,
    int subIndex,
    int width,
    int height,
    int arrayIndex,
    Instant lastChangeDate,
    int numChannels
) {
}
