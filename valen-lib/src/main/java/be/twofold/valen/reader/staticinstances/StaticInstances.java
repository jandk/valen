package be.twofold.valen.reader.staticinstances;

import be.twofold.valen.core.util.*;

import java.util.*;

public record StaticInstances(
    List<String> materials,
    List<String> declRenderParams,
    List<String> renderParams,
    List<String> models,
    List<StaticInstanceMaterialGroup> materialGroups,
    List<StaticInstanceGroup2> group2,
    List<StaticInstanceGroup3> group3,
    List<String> declLayers,
    List<String> modelInstanceNames,
    List<StaticInstanceModelGeometry> modelInstanceGeometries,
    List<StaticInstanceModelExtra> modelInstanceExtras,
    List<String> decalInstanceNames,
    List<StaticInstanceDeclGeometry> decalInstanceGeometries,
    List<StaticInstanceDeclExtra> decalInstanceExtras,
    List<StaticInstancePlayerStart> playerStarts,
    List<StaticInstanceLayerStateChange> layerStateChanges
) {
    public static StaticInstances read(BetterBuffer buffer) {
        buffer.expectInt(1);

        var materials = buffer.getStructs(buffer.getInt() - 1, BetterBuffer::getString);
        var declRenderParams = buffer.getStructs(buffer.getInt() - 1, BetterBuffer::getString);
        var renderParams = buffer.getStructs(buffer.getInt(), BetterBuffer::getString);
        var models = buffer.getStructs(buffer.getInt(), BetterBuffer::getString);
        var materialGroups = buffer.getStructs(buffer.getInt(), StaticInstanceMaterialGroup::read);
        var group2 = buffer.getStructs(buffer.getInt(), StaticInstanceGroup2::read);
        var group3 = buffer.getStructs(buffer.getInt(), StaticInstanceGroup3::read);
        var declLayers = buffer.getStructs(buffer.getInt(), BetterBuffer::getString);

        var modelInstanceCount = buffer.getInt();
        var modelInstanceNames = buffer.getStructs(modelInstanceCount, BetterBuffer::getString);
        var modelInstanceGeometries = buffer.getStructs(modelInstanceCount, StaticInstanceModelGeometry::read);
        var modelInstanceExtras = buffer.getStructs(buffer.getInt(), StaticInstanceModelExtra::read);

        var decalInstanceCount = buffer.getInt();
        var decalInstanceNames = buffer.getStructs(decalInstanceCount, BetterBuffer::getString);
        var decalInstanceGeometries = buffer.getStructs(decalInstanceCount, StaticInstanceDeclGeometry::read);
        var decalInstanceExtras = buffer.getStructs(buffer.getInt(), StaticInstanceDeclExtra::read);

        var playerStarts = buffer.getStructs(buffer.getInt(), StaticInstancePlayerStart::read);
        var layerStateChanges = buffer.getStructs(buffer.getInt(), StaticInstanceLayerStateChange::read);

        return new StaticInstances(
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
