package org.redeye.valen.game.spacemarines2.types.terrain;

public class TerrainLayer {
    private Float sizeMetersInvX;
    private Float sizeMetersInvY;
    private Float unkFloat;
    private Float softness;
    private Float wetnessAlbedoFactor;
    private Float wetnessRoughnessFactor;
    private Float unkFloat2;
    private Float metalness;
    private Float texelDensity;
    private TerrainLayerTerraformingParams terraforming;

    public Float getSizeMetersInvX() {
        return sizeMetersInvX;
    }

    public void setSizeMetersInvX(Float sizeMetersInvX) {
        this.sizeMetersInvX = sizeMetersInvX;
    }

    public Float getSizeMetersInvY() {
        return sizeMetersInvY;
    }

    public void setSizeMetersInvY(Float sizeMetersInvY) {
        this.sizeMetersInvY = sizeMetersInvY;
    }

    public Float getUnkFloat() {
        return unkFloat;
    }

    public void setUnkFloat(Float unkFloat) {
        this.unkFloat = unkFloat;
    }

    public Float getSoftness() {
        return softness;
    }

    public void setSoftness(Float softness) {
        this.softness = softness;
    }

    public Float getWetnessAlbedoFactor() {
        return wetnessAlbedoFactor;
    }

    public void setWetnessAlbedoFactor(Float wetnessAlbedoFactor) {
        this.wetnessAlbedoFactor = wetnessAlbedoFactor;
    }

    public Float getWetnessRoughnessFactor() {
        return wetnessRoughnessFactor;
    }

    public void setWetnessRoughnessFactor(Float wetnessRoughnessFactor) {
        this.wetnessRoughnessFactor = wetnessRoughnessFactor;
    }

    public Float getUnkFloat2() {
        return unkFloat2;
    }

    public void setUnkFloat2(Float unkFloat2) {
        this.unkFloat2 = unkFloat2;
    }

    public Float getMetalness() {
        return metalness;
    }

    public void setMetalness(Float metalness) {
        this.metalness = metalness;
    }

    public Float getTexelDensity() {
        return texelDensity;
    }

    public void setTexelDensity(Float texelDensity) {
        this.texelDensity = texelDensity;
    }

    public TerrainLayerTerraformingParams getTerraforming() {
        return terraforming;
    }

    public void setTerraforming(TerrainLayerTerraformingParams terraforming) {
        this.terraforming = terraforming;
    }
}
