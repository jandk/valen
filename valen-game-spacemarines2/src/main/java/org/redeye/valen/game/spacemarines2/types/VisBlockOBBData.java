package org.redeye.valen.game.spacemarines2.types;

public class VisBlockOBBData {
    private Box visBox;
    private Float maxHideDist2;
    private Float maxSMFactor2;

    public Box getVisBox() {
        return visBox;
    }

    public void setVisBox(Box visBox) {
        this.visBox = visBox;
    }

    public Float getMaxHideDist2() {
        return maxHideDist2;
    }

    public void setMaxHideDist2(Float maxHideDist2) {
        this.maxHideDist2 = maxHideDist2;
    }

    public Float getMaxSMFactor2() {
        return maxSMFactor2;
    }

    public void setMaxSMFactor2(Float maxSMFactor2) {
        this.maxSMFactor2 = maxSMFactor2;
    }
}