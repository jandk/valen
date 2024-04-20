package be.twofold.valen.reader.mapfilestaticinstances;

import be.twofold.valen.core.util.*;

import java.util.*;

public record MapFileStaticInstances(
    List<String> materials,
    List<String> declRenderParams,
    List<String> renderParams,
    List<String> models,
    List<MapFileStaticInstancesMaterialGroup> materialGroups,
    List<MapFileStaticInstancesGroup2> group2,
    List<MapFileStaticInstancesGroup3> group3,
    List<String> declLayers,
    List<String> modelInstanceNames,
    List<MapFileStaticInstancesModelGeometry> modelInstanceGeometries,
    List<MapFileStaticInstancesModelExtra> modelInstanceExtras,
    List<String> decalInstanceNames,
    List<MapFileStaticInstancesDeclGeometry> decalInstanceGeometries,
    List<MapFileStaticInstancesDeclExtra> decalInstanceExtras,
    List<MapFileStaticInstancesPlayerStart> playerStarts,
    List<MapFileStaticInstancesLayerStateChange> layerStateChanges
) {
    public static MapFileStaticInstances read(BetterBuffer buffer) {
        buffer.expectInt(1);

        var materials = buffer.getStructs(buffer.getInt() - 1, BetterBuffer::getString);
        var declRenderParams = buffer.getStructs(buffer.getInt() - 1, BetterBuffer::getString);
        var renderParams = buffer.getStructs(buffer.getInt(), BetterBuffer::getString);
        var models = buffer.getStructs(buffer.getInt(), BetterBuffer::getString);
        var materialGroups = buffer.getStructs(buffer.getInt(), MapFileStaticInstancesMaterialGroup::read);
        var group2 = buffer.getStructs(buffer.getInt(), MapFileStaticInstancesGroup2::read);
        var group3 = buffer.getStructs(buffer.getInt(), MapFileStaticInstancesGroup3::read);
        var declLayers = buffer.getStructs(buffer.getInt(), BetterBuffer::getString);

        var modelInstanceCount = buffer.getInt();
        var modelInstanceNames = buffer.getStructs(modelInstanceCount, BetterBuffer::getString);
        var modelInstanceGeometries = buffer.getStructs(modelInstanceCount, MapFileStaticInstancesModelGeometry::read);
        var modelInstanceExtras = buffer.getStructs(buffer.getInt(), MapFileStaticInstancesModelExtra::read);

        var decalInstanceCount = buffer.getInt();
        var decalInstanceNames = buffer.getStructs(decalInstanceCount, BetterBuffer::getString);
        var decalInstanceGeometries = buffer.getStructs(decalInstanceCount, MapFileStaticInstancesDeclGeometry::read);
        var decalInstanceExtras = buffer.getStructs(buffer.getInt(), MapFileStaticInstancesDeclExtra::read);

        var playerStarts = buffer.getStructs(buffer.getInt(), MapFileStaticInstancesPlayerStart::read);
        var layerStateChanges = buffer.getStructs(buffer.getInt(), MapFileStaticInstancesLayerStateChange::read);

        return new MapFileStaticInstances(
            materials,
            declRenderParams,
            renderParams,
            models,
            materialGroups,
            group2,
            group3,
            declLayers,
            modelInstanceNames,
            modelInstanceGeometries,
            modelInstanceExtras,
            decalInstanceNames,
            decalInstanceGeometries,
            decalInstanceExtras,
            playerStarts,
            layerStateChanges
        );
    }

    @Override
    public String toString() {
        return "StaticInstances(" +
            "materials=[" + materials.size() + " values], " +
            "declRenderParams=[" + declRenderParams.size() + " values], " +
            "renderParams=[" + renderParams.size() + " values], " +
            "models=[" + models.size() + " values], " +
            "materialGroups=[" + materialGroups.size() + " values], " +
            "group2=[" + group2.size() + " values], " +
            "group3=[" + group3.size() + " values], " +
            "declLayers=[" + declLayers.size() + " values], " +
            "modelInstanceNames=[" + modelInstanceNames.size() + " values], " +
            "modelInstanceGeometries=[" + modelInstanceGeometries.size() + " values], " +
            "modelInstanceExtras=[" + modelInstanceExtras.size() + " values], " +
            "decalInstanceNames=[" + decalInstanceNames.size() + " values], " +
            "decalInstanceGeometries=[" + decalInstanceGeometries.size() + " values], " +
            "decalInstanceExtras=[" + decalInstanceExtras.size() + " values], " +
            "playerStarts=[" + playerStarts.size() + " values], " +
            "layerStateChanges=[" + layerStateChanges.size() + " values]" +
            ")";
    }
}
