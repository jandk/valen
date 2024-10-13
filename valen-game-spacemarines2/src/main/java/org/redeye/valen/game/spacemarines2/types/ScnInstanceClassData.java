package org.redeye.valen.game.spacemarines2.types;

import org.redeye.valen.game.spacemarines2.psSection.*;

public class ScnInstanceClassData {
    private PsSectionValue.PsSectionObject ps;
    private String name;

    public PsSectionValue.PsSectionObject getPs() {
        return ps;
    }

    public void setPs(PsSectionValue.PsSectionObject ps) {
        this.ps = ps;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
