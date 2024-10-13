package org.redeye.valen.game.spacemarines2.types.lwi;


import be.twofold.valen.core.io.*;

import java.io.*;
import java.util.*;

public record LwiContainerStatic(
    String componentTag,
    List<LwiElement> elementList,
    List<LwiElementData> elementDataList
) implements LwiContainer {

    public static LwiContainerStatic read(DataSource source, int version) throws IOException {
        String componentTag = source.readPString();
        var elementCount = source.readInt();
        List<LwiElement> elementList = new ArrayList<>(elementCount);
        for (int i = 0; i < elementCount; i++) {
            elementList.add(LwiElement.read(source, version));
        }

        var elementDataCount = source.readInt();
        List<LwiElementData> elementDataList = new ArrayList<>();
        for (int i = 0; i < elementDataCount; i++) {
            elementDataList.add(LwiElementData.read(source, version));
        }
        return new LwiContainerStatic(componentTag, elementList, elementDataList);
    }
}
