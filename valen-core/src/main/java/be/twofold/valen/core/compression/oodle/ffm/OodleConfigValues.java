package be.twofold.valen.core.compression.oodle.ffm;

import java.lang.foreign.*;
import java.lang.invoke.*;

public final class OodleConfigValues {
    private static final MemoryLayout LAYOUT = MemoryLayout.structLayout(
        ValueLayout.JAVA_INT.withByteAlignment(1).withName("m_OodleLZ_LW_LRM_step"),
        ValueLayout.JAVA_INT.withByteAlignment(1).withName("m_OodleLZ_LW_LRM_hashLength"),
        ValueLayout.JAVA_INT.withByteAlignment(1).withName("m_OodleLZ_LW_LRM_jumpbits"),
        ValueLayout.JAVA_INT.withByteAlignment(1).withName("m_OodleLZ_Decoder_Max_Stack_Size"),
        ValueLayout.JAVA_INT.withByteAlignment(1).withName("m_OodleLZ_Small_Buffer_LZ_Fallback_Size_Unused"),
        ValueLayout.JAVA_INT.withByteAlignment(1).withName("m_OodleLZ_BackwardsCompatible_MajorVersion"),
        ValueLayout.JAVA_INT.withByteAlignment(1).withName("m_oodle_header_version")
    ).withName("OodleConfigValues");

    private static final VarHandle VH_OodleLZ_LW_LRM_step = LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("m_OodleLZ_LW_LRM_step"));
    private static final VarHandle VH_OodleLZ_LW_LRM_hashLength = LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("m_OodleLZ_LW_LRM_hashLength"));
    private static final VarHandle VH_OodleLZ_LW_LRM_jumpbits = LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("m_OodleLZ_LW_LRM_jumpbits"));
    private static final VarHandle VH_OodleLZ_Decoder_Max_Stack_Size = LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("m_OodleLZ_Decoder_Max_Stack_Size"));
    private static final VarHandle VH_OodleLZ_Small_Buffer_LZ_Fallback_Size_Unused = LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("m_OodleLZ_Small_Buffer_LZ_Fallback_Size_Unused"));
    private static final VarHandle VH_OodleLZ_BackwardsCompatible_MajorVersion = LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("m_OodleLZ_BackwardsCompatible_MajorVersion"));
    private static final VarHandle VH_oodle_header_version = LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("m_oodle_header_version"));

    private final MemorySegment segment;

    public OodleConfigValues(Arena arena) {
        segment = arena.allocate(LAYOUT);
    }

    public MemorySegment segment() {
        return segment;
    }

    public int m_OodleLZ_LW_LRM_step() {
        return (int) VH_OodleLZ_LW_LRM_step.get(segment, 0);
    }

    public int m_OodleLZ_LW_LRM_hashLength() {
        return (int) VH_OodleLZ_LW_LRM_hashLength.get(segment, 0);
    }

    public int m_OodleLZ_LW_LRM_jumpbits() {
        return (int) VH_OodleLZ_LW_LRM_jumpbits.get(segment, 0);
    }

    public int m_OodleLZ_Decoder_Max_Stack_Size() {
        return (int) VH_OodleLZ_Decoder_Max_Stack_Size.get(segment, 0);
    }

    public int m_OodleLZ_Small_Buffer_LZ_Fallback_Size_Unused() {
        return (int) VH_OodleLZ_Small_Buffer_LZ_Fallback_Size_Unused.get(segment, 0);
    }

    public int m_OodleLZ_BackwardsCompatible_MajorVersion() {
        return (int) VH_OodleLZ_BackwardsCompatible_MajorVersion.get(segment, 0);
    }

    public int m_oodle_header_version() {
        return (int) VH_oodle_header_version.get(segment, 0);
    }
}
