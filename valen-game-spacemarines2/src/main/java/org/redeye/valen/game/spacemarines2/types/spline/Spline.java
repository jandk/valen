package org.redeye.valen.game.spacemarines2.types.spline;

public class Spline {
    private SplineType type;
    private int compressedDataSize;
    private int valueDim;
    private int dataDim;
    private int pointCount;
    private int dataSize;

    private SplineData data;

    public SplineType getType() {
        return type;
    }

    public void setType(SplineType type) {
        this.type = type;
    }

    public int getCompressedDataSize() {
        return compressedDataSize;
    }

    public void setCompressedDataSize(int compressedDataSize) {
        this.compressedDataSize = compressedDataSize;
    }

    public SplineData getData() {
        return data;
    }

    public void setData(SplineData data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Spline{" +
            "type=" + type +
            ", compressedDataSize=" + compressedDataSize +
            ", valueDim=" + valueDim +
            ", dataDim=" + dataDim +
            ", pointCount=" + pointCount +
            ", dataSize=" + dataSize +
            ", data=" + data +
            '}';
    }

    public int getValueDim() {
        return valueDim;
    }

    public void setValueDim(int valueDim) {
        this.valueDim = valueDim;
    }

    public int getDataDim() {
        return dataDim;
    }

    public void setDataDim(int dataDim) {
        this.dataDim = dataDim;
    }

    public int getPointCount() {
        return pointCount;
    }

    public void setPointCount(int pointCount) {
        this.pointCount = pointCount;
    }

    public int getDataSize() {
        return dataSize;
    }

    public void setDataSize(int dataSize) {
        this.dataSize = dataSize;
    }
}
