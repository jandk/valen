package org.redeye.dmf;



public class DMFTexture {
    public final String name;
    public final int bufferId;
    public String usageType;

    public DMFTexture( String name, int bufferId) {
        this.name = name;
        this.bufferId = bufferId;
    }

    
    public static DMFTexture nonExportableTexture( String name) {
        return new DMFTexture(name, -1);
    }
}
