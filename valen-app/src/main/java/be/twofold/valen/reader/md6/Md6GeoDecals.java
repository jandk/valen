package be.twofold.valen.reader.md6;

public record Md6GeoDecals(
    String materialName,
    int[] geoDecalCounts,
    int[][] geoDecalIndices
) {
}
