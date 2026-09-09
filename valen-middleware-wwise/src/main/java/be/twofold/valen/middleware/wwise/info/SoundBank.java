package be.twofold.valen.middleware.wwise.info;

import be.twofold.valen.middleware.wwise.shared.*;

import javax.xml.stream.*;
import java.util.*;

public record SoundBank(
    BankId id,
    String language,
    String shortName,
    String path,
    List<Event> includedEvents,
    List<Integer> referencedStreamedFiles,
    List<MediaFile> includedMemoryFiles,
    List<MediaFile> excludedMemoryFiles
) {
    public static SoundBank read(XMLStreamReader reader) throws XMLStreamException {
        var id = BankId.parse(reader.getAttributeValue(null, "Id"));
        var language = reader.getAttributeValue(null, "Language");

        var shortName = (String) null;
        var path = (String) null;
        var includedEvents = List.<Event>of();
        var referencedStreamedFiles = List.<Integer>of();
        var includedMemoryFiles = List.<MediaFile>of();
        var excludedMemoryFiles = List.<MediaFile>of();

        while (reader.hasNext()) {
            var event = reader.next();
            if (event == XMLStreamConstants.START_ELEMENT) {
                switch (reader.getLocalName()) {
                    case "ShortName" -> shortName = reader.getElementText();
                    case "Path" -> path = reader.getElementText();
                    case "IncludedEvents" ->
                        includedEvents = InfoXmlReader.readList(reader, "IncludedEvents", "Event", Event::read);
                    case "ReferencedStreamedFiles" ->
                        referencedStreamedFiles = InfoXmlReader.readList(reader, "ReferencedStreamedFiles", "File", MediaFile::readId);
                    case "IncludedMemoryFiles" ->
                        includedMemoryFiles = InfoXmlReader.readList(reader, "IncludedMemoryFiles", "File", MediaFile::read);
                    case "ExcludedMemoryFiles" ->
                        excludedMemoryFiles = InfoXmlReader.readList(reader, "ExcludedMemoryFiles", "File", MediaFile::read);
                    default -> throw new XMLStreamException("Unknown element in SoundBank: " + reader.getLocalName());
                }
            } else if (event == XMLStreamConstants.END_ELEMENT && reader.getLocalName().equals("SoundBank")) {
                break;
            }
        }

        return new SoundBank(
            id,
            language,
            shortName,
            path,
            includedEvents,
            referencedStreamedFiles,
            includedMemoryFiles,
            excludedMemoryFiles
        );
    }
}
