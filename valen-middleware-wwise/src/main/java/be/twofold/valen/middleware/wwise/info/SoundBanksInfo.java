package be.twofold.valen.middleware.wwise.info;

import javax.xml.stream.*;
import java.util.*;

public record SoundBanksInfo(
    String platform,
    String basePlatform,
    int schemaVersion,
    int soundBankVersion,
    RootPaths rootPaths,
    List<MediaFile> streamedFiles,
    List<SoundBank> soundBanks
) {
    public static SoundBanksInfo read(XMLStreamReader reader) throws XMLStreamException {
        var platform = reader.getAttributeValue(null, "Platform");
        var basePlatform = reader.getAttributeValue(null, "BasePlatform");
        var schemaVersion = Integer.parseInt(reader.getAttributeValue(null, "SchemaVersion"));
        var soundBankVersion = Integer.parseInt(reader.getAttributeValue(null, "SoundbankVersion"));

        var rootPaths = (RootPaths) null;
        var streamedFiles = List.<MediaFile>of();
        var soundBanks = List.<SoundBank>of();

        while (reader.hasNext()) {
            var event = reader.next();
            if (event == XMLStreamConstants.START_ELEMENT) {
                switch (reader.getLocalName()) {
                    case "RootPaths" -> rootPaths = RootPaths.read(reader);
                    case "StreamedFiles" ->
                        streamedFiles = XmlReader.readList(reader, "StreamedFiles", "File", MediaFile::read);
                    case "SoundBanks" ->
                        soundBanks = XmlReader.readList(reader, "SoundBanks", "SoundBank", SoundBank::read);
                    case "DialogueEvents", "MediaFilesNotInAnyBank" -> {
                    }
                    default ->
                        throw new XMLStreamException("Unknown element in SoundBanksInfo: " + reader.getLocalName());
                }
            } else if (event == XMLStreamConstants.END_ELEMENT && reader.getLocalName().equals("SoundBanksInfo")) {
                break;
            }
        }

        return new SoundBanksInfo(
            platform,
            basePlatform,
            schemaVersion,
            soundBankVersion,
            rootPaths,
            streamedFiles,
            soundBanks
        );
    }
}
