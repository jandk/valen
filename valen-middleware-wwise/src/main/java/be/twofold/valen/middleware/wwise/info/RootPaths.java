package be.twofold.valen.middleware.wwise.info;

import javax.xml.stream.*;

public record RootPaths(
    String projectRoot,
    String sourceFilesRoot,
    String soundBanksRoot,
    String externalSourcesInputFile,
    String externalSourcesOutputRoot
) {
    public static RootPaths read(XMLStreamReader reader) throws XMLStreamException {
        var projectRoot = (String) null;
        var sourceFilesRoot = (String) null;
        var soundBanksRoot = (String) null;
        var externalSourcesInputFile = (String) null;
        var externalSourcesOutputRoot = (String) null;

        while (reader.hasNext()) {
            var event = reader.next();
            if (event == XMLStreamConstants.START_ELEMENT) {
                var text = reader.getElementText();
                switch (reader.getLocalName()) {
                    case "ProjectRoot" -> projectRoot = text;
                    case "SourceFilesRoot" -> sourceFilesRoot = text;
                    case "SoundBanksRoot" -> soundBanksRoot = text;
                    case "ExternalSourcesInputFile" -> externalSourcesInputFile = text;
                    case "ExternalSourcesOutputRoot" -> externalSourcesOutputRoot = text;
                    default -> throw new XMLStreamException("Unknown element in RootPaths: " + reader.getLocalName());
                }
            } else if (event == XMLStreamConstants.END_ELEMENT && reader.getLocalName().equals("RootPaths")) {
                break;
            }
        }

        return new RootPaths(
            projectRoot,
            sourceFilesRoot,
            soundBanksRoot,
            externalSourcesInputFile,
            externalSourcesOutputRoot
        );
    }
}
