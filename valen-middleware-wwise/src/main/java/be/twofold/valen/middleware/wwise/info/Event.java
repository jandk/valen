package be.twofold.valen.middleware.wwise.info;

import javax.xml.stream.*;
import java.util.*;

public record Event(
    int id,
    String name,
    DurationType durationType,
    OptionalDouble durationMin,
    OptionalDouble durationMax,
    OptionalDouble maxAttenuation
) {
    public static Event read(XMLStreamReader reader) {
        var id = Integer.parseUnsignedInt(reader.getAttributeValue(null, "Id"));
        var name = reader.getAttributeValue(null, "Name");
        var durationType = DurationType.fromString(reader.getAttributeValue(null, "DurationType"));
        var durationMin = readDouble(reader, "DurationMin");
        var durationMax = readDouble(reader, "DurationMax");
        var maxAttenuation = readDouble(reader, "MaxAttenuation");

        return new Event(
            id,
            name,
            durationType,
            durationMin,
            durationMax,
            maxAttenuation
        );
    }

    private static OptionalDouble readDouble(XMLStreamReader reader, String name) {
        var value = reader.getAttributeValue(null, name);
        if (value == null) {
            return OptionalDouble.empty();
        }
        return OptionalDouble.of(value.equals("Infinite") ? Double.POSITIVE_INFINITY : Double.parseDouble(value));
    }
}
