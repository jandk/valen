package org.redeye.valen.game.spacemarines2.types.template;

import be.twofold.valen.core.io.*;
import org.redeye.valen.game.spacemarines2.fio.*;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

public record LodDefRecord(
    @FioField(serializer = FioInt16Serializer.class)
    Short objId,

    @FioField(serializer = FioInt8Serializer.class)
    Byte index,

    @FioField(serializer = FioInt8Serializer.class, flags = 9)
    Byte isLastLodUpToInfinity
) {
    public static void main(String[] args) throws ReflectiveOperationException, IOException {
        RecordComponent[] recordComponents = LodDefRecord.class.getRecordComponents();
        var a = new Object[recordComponents.length];
        for (RecordComponent recordComponent : recordComponents) {
            var deserializerClass = recordComponent.getAnnotation(FioField.class).serializer();
            var deserializer = deserializerClass.getConstructor().newInstance();
            var value = deserializer.load(DataSource.fromArray(new byte[]{0}));
            a[0] = value;
        }
        var types = Arrays.stream(recordComponents).map(RecordComponent::getType).toArray(Class[]::new);
        Constructor<LodDefRecord> constructor = LodDefRecord.class.getConstructor(types);
        var lodDef = constructor.newInstance(a);
        System.out.println(lodDef);
    }
}
