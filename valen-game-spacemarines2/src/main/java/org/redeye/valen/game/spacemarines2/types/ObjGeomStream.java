package org.redeye.valen.game.spacemarines2.types;


import java.util.*;

public class ObjGeomStream {
    public Set<FVF> fvf;
    public Integer size;
    public Short stride;
    public Integer vBuffOffset;
    public Long setId;

    public int state;
    public BitSet flags = new BitSet();
    public byte[] data;

    public void setFvf(Set<FVF> fvf) {
        this.fvf = fvf;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public void setStride(Short stride) {
        this.stride = stride;
    }

    public void setvBuffOffset(Integer vBuffOffset) {
        this.vBuffOffset = vBuffOffset;
    }

    public void setSetId(Long setId) {
        this.setId = setId;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof ObjGeomStream that)) return false;
        return state == that.state &&
            Objects.equals(fvf, that.fvf) &&
            Objects.equals(size, that.size) &&
            Objects.equals(stride, that.stride) &&
            Objects.equals(vBuffOffset, that.vBuffOffset) &&
            Objects.equals(setId, that.setId) &&
            Objects.equals(flags, that.flags) &&
            Objects.deepEquals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fvf, size, stride, vBuffOffset, setId, state, flags, Arrays.hashCode(data));
    }
}
