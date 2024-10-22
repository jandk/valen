package org.redeye.valen.game.spacemarines2.types.terrain;

import java.util.*;

public class Terrain {
    private Integer resXArr;
    private Integer resZArr;
    private Float unkFloat;
    private Float unkFloat2;
    private List<Float> heightArrDeprecatedFloat;
    private BitSet terrainHolesMask;
    private List<Integer> staticGameMaterialsOld;
    private TerrainMaterials terrainMaterials;
    private List<Byte> extrusionArrDeprecated;
    private List<Byte> wetnessArrDeprecated;
    private List<Byte> weights1ArrDeprecated;
    private List<Byte> weights2ArrDeprecated;
    private TerrainDeformPersistentData deformPersistentData;
    private List<Byte> fogOfWarArr;
    private List<Byte> excavatabilityArrDeprecated;
    private List<Short> sandArrDeprecated;
    private List<Short> asphaltArrDeprecated;
    private List<Short> heightArrDeprecatedUINT16;
    private List<Byte> staticGameMaterialsDeprecated;
    private List<Byte> extrusionArrAutoDeprecated;
    private List<Byte> malleabilityArrDeprecated;
    private List<Byte> minViscosityArrDeprecated;
    private TerrainBlockArray<Short> heightArr;
    private TerrainBlockArray<Byte> extrusionArrAuto;
    private TerrainBlockArray<Byte> excavatabilityArr;
    private TerrainBlockArray<Byte> extrusionArr;
    private TerrainBlockArray<Byte> wetnessArr;
    private TerrainBlockArray<Short> sandArr;
    private TerrainBlockArray<Short> asphaltArr;
    private TerrainBlockArray<Byte> malleabilityArr;
    private TerrainBlockArray<Byte> minViscosityArr;
    private TerrainBlockArray<Byte> staticGameMaterials;
    private List<Byte> physExportData;
    private TerrainBlockArray<Byte> grassHideMaskArr;
    private TerrainBlockArray<Byte> aiGrassHeightIndexesArr;
    private List<Float> aiGrassHeights;

    public Integer getResXArr() {
        return resXArr;
    }

    public void setResXArr(Integer resXArr) {
        this.resXArr = resXArr;
    }

    public Integer getResZArr() {
        return resZArr;
    }

    public void setResZArr(Integer resZArr) {
        this.resZArr = resZArr;
    }

    public Float getUnkFloat() {
        return unkFloat;
    }

    public void setUnkFloat(Float unkFloat) {
        this.unkFloat = unkFloat;
    }

    public Float getUnkFloat2() {
        return unkFloat2;
    }

    public void setUnkFloat2(Float unkFloat2) {
        this.unkFloat2 = unkFloat2;
    }

    public List<Byte> getPhysExportData() {
        return physExportData;
    }

    public void setPhysExportData(List<Byte> physExportData) {
        this.physExportData = physExportData;
    }

    public BitSet getTerrainHolesMask() {
        return terrainHolesMask;
    }

    public void setTerrainHolesMask(BitSet terrainHolesMask) {
        this.terrainHolesMask = terrainHolesMask;
    }

    public List<Integer> getStaticGameMaterialsOld() {
        return staticGameMaterialsOld;
    }

    public void setStaticGameMaterialsOld(List<Integer> staticGameMaterialsOld) {
        this.staticGameMaterialsOld = staticGameMaterialsOld;
    }

    public TerrainMaterials getTerrainMaterials() {
        return terrainMaterials;
    }

    public void setTerrainMaterials(TerrainMaterials terrainMaterials) {
        this.terrainMaterials = terrainMaterials;
    }

    public List<Byte> getExtrusionArrDeprecated() {
        return extrusionArrDeprecated;
    }

    public void setExtrusionArrDeprecated(List<Byte> extrusionArrDeprecated) {
        this.extrusionArrDeprecated = extrusionArrDeprecated;
    }

    public List<Byte> getWetnessArrDeprecated() {
        return wetnessArrDeprecated;
    }

    public void setWetnessArrDeprecated(List<Byte> wetnessArrDeprecated) {
        this.wetnessArrDeprecated = wetnessArrDeprecated;
    }

    public List<Byte> getWeights1ArrDeprecated() {
        return weights1ArrDeprecated;
    }

    public void setWeights1ArrDeprecated(List<Byte> weights1ArrDeprecated) {
        this.weights1ArrDeprecated = weights1ArrDeprecated;
    }

    public List<Byte> getWeights2ArrDeprecated() {
        return weights2ArrDeprecated;
    }

    public void setWeights2ArrDeprecated(List<Byte> weights2ArrDeprecated) {
        this.weights2ArrDeprecated = weights2ArrDeprecated;
    }

    public TerrainDeformPersistentData getDeformPersistentData() {
        return deformPersistentData;
    }

    public void setDeformPersistentData(TerrainDeformPersistentData deformPersistentData) {
        this.deformPersistentData = deformPersistentData;
    }

    public List<Byte> getFogOfWarArr() {
        return fogOfWarArr;
    }

    public void setFogOfWarArr(List<Byte> fogOfWarArr) {
        this.fogOfWarArr = fogOfWarArr;
    }

    public List<Byte> getExcavatabilityArrDeprecated() {
        return excavatabilityArrDeprecated;
    }

    public void setExcavatabilityArrDeprecated(List<Byte> excavatabilityArrDeprecated) {
        this.excavatabilityArrDeprecated = excavatabilityArrDeprecated;
    }

    public List<Short> getSandArrDeprecated() {
        return sandArrDeprecated;
    }

    public void setSandArrDeprecated(List<Short> sandArrDeprecated) {
        this.sandArrDeprecated = sandArrDeprecated;
    }

    public List<Short> getAsphaltArrDeprecated() {
        return asphaltArrDeprecated;
    }

    public void setAsphaltArrDeprecated(List<Short> asphaltArrDeprecated) {
        this.asphaltArrDeprecated = asphaltArrDeprecated;
    }

    public List<Short> getHeightArrDeprecatedUINT16() {
        return heightArrDeprecatedUINT16;
    }

    public void setHeightArrDeprecatedUINT16(List<Short> heightArrDeprecatedUINT16) {
        this.heightArrDeprecatedUINT16 = heightArrDeprecatedUINT16;
    }

    public List<Byte> getStaticGameMaterialsDeprecated() {
        return staticGameMaterialsDeprecated;
    }

    public void setStaticGameMaterialsDeprecated(List<Byte> staticGameMaterialsDeprecated) {
        this.staticGameMaterialsDeprecated = staticGameMaterialsDeprecated;
    }

    public List<Byte> getExtrusionArrAutoDeprecated() {
        return extrusionArrAutoDeprecated;
    }

    public void setExtrusionArrAutoDeprecated(List<Byte> extrusionArrAutoDeprecated) {
        this.extrusionArrAutoDeprecated = extrusionArrAutoDeprecated;
    }

    public List<Byte> getMalleabilityArrDeprecated() {
        return malleabilityArrDeprecated;
    }

    public void setMalleabilityArrDeprecated(List<Byte> malleabilityArrDeprecated) {
        this.malleabilityArrDeprecated = malleabilityArrDeprecated;
    }

    public List<Byte> getMinViscosityArrDeprecated() {
        return minViscosityArrDeprecated;
    }

    public void setMinViscosityArrDeprecated(List<Byte> minViscosityArrDeprecated) {
        this.minViscosityArrDeprecated = minViscosityArrDeprecated;
    }

    public TerrainBlockArray<Short> getHeightArr() {
        return heightArr;
    }

    public void setHeightArr(TerrainBlockArray<Short> heightArr) {
        this.heightArr = heightArr;
    }

    public TerrainBlockArray<Byte> getExtrusionArrAuto() {
        return extrusionArrAuto;
    }

    public void setExtrusionArrAuto(TerrainBlockArray<Byte> extrusionArrAuto) {
        this.extrusionArrAuto = extrusionArrAuto;
    }

    public TerrainBlockArray<Byte> getExcavatabilityArr() {
        return excavatabilityArr;
    }

    public void setExcavatabilityArr(TerrainBlockArray<Byte> excavatabilityArr) {
        this.excavatabilityArr = excavatabilityArr;
    }

    public TerrainBlockArray<Byte> getExtrusionArr() {
        return extrusionArr;
    }

    public void setExtrusionArr(TerrainBlockArray<Byte> extrusionArr) {
        this.extrusionArr = extrusionArr;
    }

    public TerrainBlockArray<Byte> getWetnessArr() {
        return wetnessArr;
    }

    public void setWetnessArr(TerrainBlockArray<Byte> wetnessArr) {
        this.wetnessArr = wetnessArr;
    }

    public TerrainBlockArray<Short> getSandArr() {
        return sandArr;
    }

    public void setSandArr(TerrainBlockArray<Short> sandArr) {
        this.sandArr = sandArr;
    }

    public TerrainBlockArray<Short> getAsphaltArr() {
        return asphaltArr;
    }

    public void setAsphaltArr(TerrainBlockArray<Short> asphaltArr) {
        this.asphaltArr = asphaltArr;
    }

    public TerrainBlockArray<Byte> getMalleabilityArr() {
        return malleabilityArr;
    }

    public void setMalleabilityArr(TerrainBlockArray<Byte> malleabilityArr) {
        this.malleabilityArr = malleabilityArr;
    }

    public TerrainBlockArray<Byte> getMinViscosityArr() {
        return minViscosityArr;
    }

    public void setMinViscosityArr(TerrainBlockArray<Byte> minViscosityArr) {
        this.minViscosityArr = minViscosityArr;
    }

    public TerrainBlockArray<Byte> getStaticGameMaterials() {
        return staticGameMaterials;
    }

    public void setStaticGameMaterials(TerrainBlockArray<Byte> staticGameMaterials) {
        this.staticGameMaterials = staticGameMaterials;
    }

    public TerrainBlockArray<Byte> getGrassHideMaskArr() {
        return grassHideMaskArr;
    }

    public void setGrassHideMaskArr(TerrainBlockArray<Byte> grassHideMaskArr) {
        this.grassHideMaskArr = grassHideMaskArr;
    }

    public TerrainBlockArray<Byte> getAiGrassHeightIndexesArr() {
        return aiGrassHeightIndexesArr;
    }

    public void setAiGrassHeightIndexesArr(TerrainBlockArray<Byte> aiGrassHeightIndexesArr) {
        this.aiGrassHeightIndexesArr = aiGrassHeightIndexesArr;
    }

    public List<Float> getAiGrassHeights() {
        return aiGrassHeights;
    }

    public void setAiGrassHeights(List<Float> aiGrassHeights) {
        this.aiGrassHeights = aiGrassHeights;
    }

    public List<Float> getHeightArrDeprecatedFloat() {
        return heightArrDeprecatedFloat;
    }

    public void setHeightArrDeprecatedFloat(List<Float> heightArrDeprecatedFloat) {
        this.heightArrDeprecatedFloat = heightArrDeprecatedFloat;
    }
}
