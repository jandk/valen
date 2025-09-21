package be.twofold.valen.game.eternal.reader.havokshape;

import java.util.*;
import java.util.stream.*;

public final class HavokType {
    private String name;
    private List<HavokTypeTemplateParam> templateParams = List.of();
    private HavokType parent;
    private Set<HkOption> options = Set.of();
    private int format;
    private HavokType subType;
    private int version;
    private int size;
    private int align;
    private int flags;
    private List<HavokField> fields = List.of();
    private List<HavokInterface> interfaces = List.of();
    private int attribute;
    private int hash;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<HavokTypeTemplateParam> getTemplateParams() {
        return templateParams;
    }

    public void setTemplateParams(List<HavokTypeTemplateParam> templateParams) {
        this.templateParams = templateParams;
    }

    public HavokType getParent() {
        return parent;
    }

    public void setParent(HavokType parent) {
        this.parent = parent;
    }

    public Set<HkOption> getOptions() {
        return options;
    }

    public void setOptions(Set<HkOption> options) {
        this.options = options;
    }

    public int getFormat() {
        return format;
    }

    public void setFormat(int format) {
        this.format = format;
    }

    public HavokType getSubType() {
        return subType;
    }

    public void setSubType(HavokType subType) {
        this.subType = subType;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getAlign() {
        return align;
    }

    public void setAlign(int align) {
        this.align = align;
    }

    public int getFlags() {
        return flags;
    }

    public void setFlags(int flags) {
        this.flags = flags;
    }

    public List<HavokField> getFields() {
        return fields;
    }

    public void setFields(List<HavokField> fields) {
        this.fields = fields;
    }

    public List<HavokInterface> getInterfaces() {
        return interfaces;
    }

    public void setInterfaces(List<HavokInterface> interfaces) {
        this.interfaces = interfaces;
    }

    public int getHash() {
        return hash;
    }

    public void setHash(int hash) {
        this.hash = hash;
    }

    public int getAttribute() {
        return attribute;
    }

    public void setAttribute(int attribute) {
        this.attribute = attribute;
    }

    public HavokFormatType getFormatType() {
        return switch (format & 0x1f) {
            case 0 -> HavokFormatType.Void;
            case 1 -> HavokFormatType.Opaque;
            case 2 -> HavokFormatType.Bool;
            case 3 -> HavokFormatType.String;
            case 4 -> HavokFormatType.Int;
            case 5 -> HavokFormatType.Float;
            case 6 -> HavokFormatType.Pointer;
            case 7 -> HavokFormatType.Record;
            case 8 -> HavokFormatType.Array;
            default -> throw new UnsupportedOperationException("Unsupported format type: " + format);
        };
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(name);
        if (!templateParams.isEmpty()) {
            builder.append(templateParams.stream()
                .map(HavokTypeTemplateParam::toString)
                .collect(Collectors.joining(", ", "<", ">")));
        }
        return builder.toString();
    }
}