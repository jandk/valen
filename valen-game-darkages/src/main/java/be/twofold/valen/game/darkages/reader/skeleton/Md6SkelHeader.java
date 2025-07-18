package be.twofold.valen.game.darkages.reader.skeleton;

import be.twofold.valen.core.io.BinaryReader;

import java.io.*;

/**
 * @param size                       the allocated size of the skeleton
 * @param loadedDataSize             the size of the skeleton BEFORE reallocating for dynamic data
 * @param basePoseOffset             offset of the base pose data (stores as R/S/T streams)
 * @param inverseBasePoseOffset      offset of the inverted base pose data (stored as 3x4 matrices)
 * @param parentTblHash              hash used to verify animations are loaded / played on the correct mesh
 * @param numJoints                  the number of joints in the skeleton
 * @param numJointHandles            the number of extended joints in the joint handle table
 * @param parentTblOffset            offset of the table with the parent for each joint
 * @param lastChildTblOffset         offset of the table with the last child index for each joint
 * @param jointNameHandleTblOffset   offset of the table with the name handle for each joint
 * @param jointIndexTblOffset        offset of the table with joint handle to index mapping
 * @param numUserChannels            number of user channels
 * @param userChannelHandleTblOffset offset of the table with a handle for each user channel
 * @param jointSetTblOffset          offset of the table containing skeleton remap info
 * @param boundsJointTblOffset       offset to the table of joints that contribute to animated bounds
 */
public record Md6SkelHeader(
    int size,
    int loadedDataSize,
    int basePoseOffset,
    int inverseBasePoseOffset,
    int nameHash,
    int parentTblHash,
    short numJoints,
    short numJointHandles,
    short parentTblOffset,
    short lastChildTblOffset,
    short jointNameHandleTblOffset,
    short jointIndexTblOffset,
    short numUserChannels,
    short userChannelHandleTblOffset,
    short jointSetTblOffset,
    short boundsJointTblOffset,
    float assetScale
) {
    public static Md6SkelHeader read(BinaryReader reader) throws IOException {
        int size = reader.readInt();
        int loadedDataSize = reader.readInt();
        int basePoseOffset = reader.readInt();
        int inverseBasePoseOffset = reader.readInt();
        int nameHash = reader.readInt();
        int parentTblHash = reader.readInt();
        short numJoints = reader.readShort();
        short numJointHandles = reader.readShort();
        short parentTblOffset = reader.readShort();
        short lastChildTblOffset = reader.readShort();
        short jointNameHandleTblOffset = reader.readShort();
        short jointIndexTblOffset = reader.readShort();
        short numUserChannels = reader.readShort();
        short userChannelHandleTblOffset = reader.readShort();
        short jointSetTblOffset = reader.readShort();
        short boundsJointTblOffset = reader.readShort();
        float assetScale = reader.readFloat();

        return new Md6SkelHeader(
            size,
            loadedDataSize,
            basePoseOffset,
            inverseBasePoseOffset,
            nameHash,
            parentTblHash,
            numJoints,
            numJointHandles,
            parentTblOffset,
            lastChildTblOffset,
            jointNameHandleTblOffset,
            jointIndexTblOffset,
            numUserChannels,
            userChannelHandleTblOffset,
            jointSetTblOffset,
            boundsJointTblOffset,
            assetScale
        );
    }

    public int numJoints8() {
        return numJoints + 7 & ~7;
    }

    public int numJointHandles8() {
        return numJointHandles + 7 & ~7;
    }

    public int numUserChannels8() {
        return numUserChannels + 7 & ~7;
    }
}
