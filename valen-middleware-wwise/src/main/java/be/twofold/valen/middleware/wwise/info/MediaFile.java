package be.twofold.valen.middleware.wwise.info;

import javax.xml.stream.*;
import java.util.*;

public record MediaFile(
    int id,
    String language,
    boolean usingReferenceLanguageAsStandIn,
    String shortName,
    String path,
    OptionalInt prefetchSize
) {
    static MediaFile read(XMLStreamReader reader) throws XMLStreamException {
        var id = Integer.parseUnsignedInt(reader.getAttributeValue(null, "Id"));
        var language = reader.getAttributeValue(null, "Language");
        var usingReferenceLanguageAsStandIn = Boolean.parseBoolean(reader.getAttributeValue(null, "UsingReferenceLanguageAsStandIn"));

        var shortName = (String) null;
        var path = (String) null;
        var prefetchSize = OptionalInt.empty();

        while (reader.hasNext()) {
            var event = reader.next();
            if (event == XMLStreamConstants.START_ELEMENT) {
                var text = reader.getElementText();
                switch (reader.getLocalName()) {
                    case "ShortName" -> shortName = text;
                    case "Path" -> path = text;
                    case "PrefetchSize" -> prefetchSize = OptionalInt.of(Integer.parseInt(text));
                    default -> throw new XMLStreamException("Unknown element in File: " + reader.getLocalName());
                }
            } else if (event == XMLStreamConstants.END_ELEMENT && reader.getLocalName().equals("File")) {
                break;
            }
        }

        return new MediaFile(id, language, usingReferenceLanguageAsStandIn, shortName, path, prefetchSize);
    }

    static Integer readId(XMLStreamReader reader) {
        return Integer.parseUnsignedInt(reader.getAttributeValue(null, "Id"));
    }
}
