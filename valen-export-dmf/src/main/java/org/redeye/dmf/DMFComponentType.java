package org.redeye.dmf;



public enum DMFComponentType {
    UNSIGNED_BYTE("UnsignedByte", 1),
    UNSIGNED_BYTE_NORMALIZED("UnsignedByteNormalized", 1),
    SIGNED_BYTE("SignedByte", 1),
    SIGNED_BYTE_NORMALIZED("SignedByte", 1),
    SIGNED_SHORT("SignedShort", 2),
    SIGNED_SHORT_NORMALIZED("SignedShortNormalized", 2),
    UNSIGNED_SHORT("UnsignedShort", 2),
    UNSIGNED_SHORT_NORMALIZED("UnsignedShortNormalized", 2),
    HALF_FLOAT("HalfFloat", 2),
    SIGNED_INT("SignedInt", 4),
    SIGNED_INT_NORMALIZED("SignedIntNormalized", 4),
    UNSIGNED_INT("UnsignedInt", 4),
    UNSIGNED_INT_NORMALIZED("UnsignedIntNormalized", 4),
    FLOAT("Float", 4),
    X10Y10Z10W2NORMALIZED("X10Y10Z10W2Normalized", 4);

    private final String typeName;
    private final int size;

    DMFComponentType(String typeName, int size) {
        this.typeName = typeName;
        this.size = size;
    }

    public int getSize() {
        return size;
    }

    
    public String getTypeName() {
        return typeName;
    }

    
    public static DMFComponentType fromString( String text) {
        for (DMFComponentType b : DMFComponentType.values()) {
            if (b.typeName.equalsIgnoreCase(text)) {
                return b;
            }
        }
        throw new EnumConstantNotPresentException(DMFComponentType.class, text);
    }
}
