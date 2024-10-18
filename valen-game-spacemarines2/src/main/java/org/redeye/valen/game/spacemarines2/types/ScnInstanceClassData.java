package org.redeye.valen.game.spacemarines2.types;

import com.google.gson.*;

public class ScnInstanceClassData {
    private JsonObject ps;
    private String name;

    public JsonObject getPs() {
        return ps;
    }

    public void setPs(JsonObject ps) {
        this.ps = ps;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
