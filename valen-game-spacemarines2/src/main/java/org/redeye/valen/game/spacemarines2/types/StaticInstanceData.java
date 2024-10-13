package org.redeye.valen.game.spacemarines2.types;

import be.twofold.valen.core.math.*;

import java.util.*;

public class StaticInstanceData {
    private UUID sceneGuid;
    private Box staticInstAABB;
    private List<InteractiveItem> interactiveItemsList;
    private List<Integer> visBlockData;
    private List<Vector4> visBlockPlacementData;
    private List<BlockInfo> instBlockData;
    private List<InstanceDataStatic> staticInstData;
    private List<VisBlockOBBData> visBlockObbData;

    public UUID getSceneGuid() {
        return sceneGuid;
    }

    public void setSceneGuid(UUID sceneGuid) {
        this.sceneGuid = sceneGuid;
    }

    public Box getStaticInstAABB() {
        return staticInstAABB;
    }

    public void setStaticInstAABB(Box staticInstAABB) {
        this.staticInstAABB = staticInstAABB;
    }

    public List<InteractiveItem> getInteractiveItemsList() {
        return interactiveItemsList;
    }

    public void setInteractiveItemsList(List<InteractiveItem> interactiveItemsList) {
        this.interactiveItemsList = interactiveItemsList;
    }

    public List<Integer> getVisBlockData() {
        return visBlockData;
    }

    public void setVisBlockData(List<Integer> visBlockData) {
        this.visBlockData = visBlockData;
    }

    public List<Vector4> getVisBlockPlacementData() {
        return visBlockPlacementData;
    }

    public void setVisBlockPlacementData(List<Vector4> visBlockPlacementData) {
        this.visBlockPlacementData = visBlockPlacementData;
    }

    public List<BlockInfo> getInstBlockData() {
        return instBlockData;
    }

    public void setInstBlockData(List<BlockInfo> instBlockData) {
        this.instBlockData = instBlockData;
    }

    public List<InstanceDataStatic> getStaticInstData() {
        return staticInstData;
    }

    public void setStaticInstData(List<InstanceDataStatic> staticInstData) {
        this.staticInstData = staticInstData;
    }

    public List<VisBlockOBBData> getVisBlockObbData() {
        return visBlockObbData;
    }

    public void setVisBlockObbData(List<VisBlockOBBData> visBlockObbData) {
        this.visBlockObbData = visBlockObbData;
    }
}
