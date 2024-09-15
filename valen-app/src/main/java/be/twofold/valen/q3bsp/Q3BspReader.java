package be.twofold.valen.q3bsp;

import be.twofold.valen.core.io.*;
import be.twofold.valen.q3bsp.model.*;

import java.io.*;
import java.util.*;

public final class Q3BspReader {
    private static final int Q3_LUMP_ENTITIES = 0;
    private static final int Q3_LUMP_SHADERS = 1;
    private static final int Q3_LUMP_PLANES = 2;
    private static final int Q3_LUMP_NODES = 3;
    private static final int Q3_LUMP_LEAFS = 4;
    private static final int Q3_LUMP_LEAFSURFACES = 5;
    private static final int Q3_LUMP_LEAFBRUSHES = 6;
    private static final int Q3_LUMP_MODELS = 7;
    private static final int Q3_LUMP_BRUSHES = 8;
    private static final int Q3_LUMP_BRUSHSIDES = 9;
    private static final int Q3_LUMP_DRAWVERTS = 10;
    private static final int Q3_LUMP_DRAWINDEXES = 11;
    private static final int Q3_LUMP_FOGS = 12;
    private static final int Q3_LUMP_SURFACES = 13;
    private static final int Q3_LUMP_LIGHTMAPS = 14;
    private static final int Q3_LUMP_LIGHTGRID = 15;
    private static final int Q3_LUMP_VISIBILITY = 16;

    private final DataSource source;

    private String entities;
    private List<Shader> shaders;
    private List<Plane> planes;
    private List<Node> nodes;
    private List<Leaf> leafs;
    private List<Integer> leafSurfaces;
    private List<Integer> leafBrushes;
    private List<Model> models;
    private List<Brush> brushes;
    private List<BrushSide> brushSides;
    private List<DrawVert> drawVerts;
    private List<Integer> drawIndexes;
    private List<Fog> fogs;
    private List<Surface> surfaces;
    private List<LightMap> lightMaps;
    private List<LightGrid> lightGrid;
    private Visibility visibility;

    public Q3BspReader(DataSource source) {
        this.source = source;
    }

    public Q3Bsp read() throws IOException {
        readBsp();

        return new Q3Bsp(
            entities,
            shaders,
            planes,
            nodes,
            leafs,
            leafSurfaces,
            leafBrushes,
            models,
            brushes,
            brushSides,
            drawVerts,
            drawIndexes,
            fogs,
            surfaces,
            lightMaps,
            lightGrid,
            visibility
        );
    }

    private void readBsp() throws IOException {
        var header = Q3BspHeader.read(source);
        for (var i = 0; i < header.lumps().size(); i++) {
            var lump = header.lumps().get(i);
            switch (i) {
                case Q3_LUMP_ENTITIES -> this.entities = readLump(lump, lump.length(), s -> new String(s.readBytes(lump.length()))).getFirst().trim();
                case Q3_LUMP_SHADERS -> this.shaders = readLump(lump, Shader.BYTES, Shader::read);
                case Q3_LUMP_PLANES -> this.planes = readLump(lump, Plane.BYTES, Plane::read);
                case Q3_LUMP_NODES -> this.nodes = readLump(lump, Node.BYTES, Node::read);
                case Q3_LUMP_LEAFS -> this.leafs = readLump(lump, Leaf.BYTES, Leaf::read);
                case Q3_LUMP_LEAFSURFACES -> this.leafSurfaces = readLump(lump, Integer.BYTES, DataSource::readInt);
                case Q3_LUMP_LEAFBRUSHES -> this.leafBrushes = readLump(lump, Integer.BYTES, DataSource::readInt);
                case Q3_LUMP_MODELS -> this.models = readLump(lump, Model.BYTES, Model::read);
                case Q3_LUMP_BRUSHES -> this.brushes = readLump(lump, Brush.BYTES, Brush::read);
                case Q3_LUMP_BRUSHSIDES -> this.brushSides = readLump(lump, BrushSide.BYTES, BrushSide::read);
                case Q3_LUMP_DRAWVERTS -> this.drawVerts = readLump(lump, DrawVert.BYTES, DrawVert::read);
                case Q3_LUMP_DRAWINDEXES -> this.drawIndexes = readLump(lump, Integer.BYTES, DataSource::readInt);
                case Q3_LUMP_FOGS -> this.fogs = readLump(lump, Fog.BYTES, Fog::read);
                case Q3_LUMP_SURFACES -> this.surfaces = readLump(lump, Surface.BYTES, Surface::read);
                case Q3_LUMP_LIGHTMAPS -> this.lightMaps = readLump(lump, LightMap.BYTES, LightMap::read);
                case Q3_LUMP_LIGHTGRID -> this.lightGrid = readLump(lump, LightGrid.BYTES, LightGrid::read);
                case Q3_LUMP_VISIBILITY -> this.visibility = readLump(lump, lump.length(), Visibility::read).getFirst();
            }
        }
    }

    private <T> List<T> readLump(Q3BspLump lump, int bytes, StructMapper<T> reader) throws IOException {
        var count = Math.divideExact(lump.length(), bytes);
        source.seek(lump.offset());
        return source.readStructs(count, reader);
    }
}
