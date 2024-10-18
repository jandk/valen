package org.redeye.valen.game.spacemarines2.types.template;


import java.util.*;

public class ObjGeomStream {
    public Set<FVF> fvf;
    public int size = 0;
    public short stride = 0;
    public int vBuffOffset = 0;
    public Long setId;

    public int state;
    public BitSet flags = new BitSet();
    public byte[] data;

    public void setFvf(Set<FVF> fvf) {
        this.fvf = fvf;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setStride(short stride) {
        this.stride = stride;
    }

    public void setvBuffOffset(int vBuffOffset) {
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