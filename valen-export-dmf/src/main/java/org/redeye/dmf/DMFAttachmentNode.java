package org.redeye.dmf;


public class DMFAttachmentNode extends DMFNode {
    public final String boneName;

    public DMFAttachmentNode(String name, String boneName, DMFTransform transform) {
        super(name, DMFNodeType.ATTACHMENT);
        this.boneName = boneName;
        this.transform = transform;
    }
}
