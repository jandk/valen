package org.redeye.valen.game.spacemarines2.types.terrain;

public class TerrainLayerTerraformingParams {
    private Boolean isStatic;
    private Float malleability;
    private String unkString;

    public Boolean getStatic() {
        return isStatic;
    }

    public void setStatic(Boolean aStatic) {
        isStatic = aStatic;
    }

    public Float getMalleability() {
        return malleability;
    }

    public void setMalleability(Float malleability) {
        this.malleability = malleability;
    }

    public String getUnkString() {
        return unkString;
    }

    public void setUnkString(String unkString) {
        this.unkString = unkString;
    }
}
