package org.redeye.dmf;



public class DMFInstance extends DMFNode {
    public final int instanceId;

    public DMFInstance( String name, int instanceId) {
        super(name, DMFNodeType.INSTANCE);
        this.instanceId = instanceId;
    }
}
