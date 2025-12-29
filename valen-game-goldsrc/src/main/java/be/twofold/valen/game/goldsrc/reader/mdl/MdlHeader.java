package be.twofold.valen.game.goldsrc.reader.mdl;

import be.twofold.valen.core.math.*;
import be.twofold.valen.game.goldsrc.reader.mdl.v10.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.util.*;

public record MdlHeader(
    String name,
    int fileSize,
    Vector3 eyePos,
    Vector3 min,
    Vector3 max,
    Vector3 bbMin,
    Vector3 bbMax,
    Set<StudioHeaderFlags> flags,
    int boneCount,
    int boneOffset,
    int boneControllersCount,
    int boneControllersOffset,
    int hitboxCount,
    int hitboxOffset,
    int sequenceCount,
    int sequenceOffset,
    int sequenceGroupsCount,
    int sequenceGroupsOffset,
    int textureCount,
    int textureOffset,
    int textureDataOffset,
    int skinRefCount,
    int skinFamiliesCount,
    int skinOffset,
    int bodyPartCount,
    int bodyPartOffset,
    int attachmentCount,
    int attachmentOffset,
    int soundCount,
    int soundOffset,
    int soundGroupCount,
    int soundGroupOffset,
    int transitionCount,
    int transitionOffset
) {
    public static MdlHeader read(BinarySource source) throws IOException {
        source.expectInt(0x54534449); // magic
        source.expectInt(10); // version

        var name = source.readString(64).trim();
        var fileSize = source.readInt();
        var eyePos = Vector3.read(source);
        var min = Vector3.read(source);
        var max = Vector3.read(source);
        var bbMin = Vector3.read(source);
        var bbMax = Vector3.read(source);
        var flags = StudioHeaderFlags.fromValue(source.readInt());
        var boneCount = source.readInt();
        var boneOffset = source.readInt();
        var boneControllersCount = source.readInt();
        var boneControllersOffset = source.readInt();
        var hitboxCount = source.readInt();
        var hitboxOffset = source.readInt();
        var sequenceCount = source.readInt();
        var sequenceOffset = source.readInt();
        var sequenceGroupsCount = source.readInt();
        var sequenceGroupsOffset = source.readInt();
        var textureCount = source.readInt();
        var textureOffset = source.readInt();
        var textureDataOffset = source.readInt();
        var skinRefCount = source.readInt();
        var skinFamiliesCount = source.readInt();
        var skinOffset = source.readInt();
        var bodyPartCount = source.readInt();
        var bodyPartOffset = source.readInt();
        var attachmentCount = source.readInt();
        var attachmentOffset = source.readInt();
        var soundCount = source.readInt();
        var soundOffset = source.readInt();
        var soundGroupCount = source.readInt();
        var soundGroupOffset = source.readInt();
        var transitionCount = source.readInt();
        var transitionOffset = source.readInt();

        return new MdlHeader(
            name,
            fileSize,
            eyePos,
            min,
            max,
            bbMin,
            bbMax,
            flags,
            boneCount,
            boneOffset,
            boneControllersCount,
            boneControllersOffset,
            hitboxCount,
            hitboxOffset,
            sequenceCount,
            sequenceOffset,
            sequenceGroupsCount,
            sequenceGroupsOffset,
            textureCount,
            textureOffset,
            textureDataOffset,
            skinRefCount,
            skinFamiliesCount,
            skinOffset,
            bodyPartCount,
            bodyPartOffset,
            attachmentCount,
            attachmentOffset,
            soundCount,
            soundOffset,
            soundGroupCount,
            soundGroupOffset,
            transitionCount,
            transitionOffset
        );
    }
}
