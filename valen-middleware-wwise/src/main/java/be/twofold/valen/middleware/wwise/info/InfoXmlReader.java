package be.twofold.valen.middleware.wwise.info;

import javax.xml.stream.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public final class InfoXmlReader {
    private InfoXmlReader() {
    }

    public static SoundBanksInfo load(Path path) throws IOException {
        try (InputStream in = new BufferedInputStream(Files.newInputStream(path))) {
            return load(in);
        }
    }

    public static SoundBanksInfo load(InputStream input) throws IOException {
        try {
            var factory = XMLInputFactory.newInstance();
            factory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
            factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);

            var reader = factory.createXMLStreamReader(input);
            while (reader.hasNext()) {
                if (reader.next() == XMLStreamConstants.START_ELEMENT
                    && reader.getLocalName().equals("SoundBanksInfo")) {
                    return SoundBanksInfo.read(reader);
                }
            }
            throw new XMLStreamException("No SoundBanksInfo element found");
        } catch (XMLStreamException e) {
            throw new IOException("Could not load soundbanks info", e);
        }
    }

    static <T> List<T> readList(
        XMLStreamReader reader,
        String container,
        String element,
        ElementReader<T> elementReader
    ) throws XMLStreamException {
        var items = new ArrayList<T>();

        while (reader.hasNext()) {
            var event = reader.next();
            if (event == XMLStreamConstants.START_ELEMENT) {
                if (!reader.getLocalName().equals(element)) {
                    throw new XMLStreamException("Unknown element in " + container + ": " + reader.getLocalName());
                }
                items.add(elementReader.read(reader));
            } else if (event == XMLStreamConstants.END_ELEMENT && reader.getLocalName().equals(container)) {
                break;
            }
        }

        return List.copyOf(items);
    }

    @FunctionalInterface
    interface ElementReader<T> {
        T read(XMLStreamReader reader) throws XMLStreamException;
    }
}
