package be.twofold.valen.ui.component.modelviewer;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.geometry.Mesh;
import be.twofold.valen.core.material.*;
import be.twofold.valen.core.material.Material;
import be.twofold.valen.core.texture.*;
import be.twofold.valen.ui.common.*;
import be.twofold.valen.ui.component.*;
import jakarta.inject.*;
import javafx.collections.*;
import javafx.scene.image.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import org.slf4j.*;
import wtf.reversed.toolbox.collect.*;

import java.io.*;
import java.util.*;

public final class ModelPresenter extends AbstractPresenter<ModelView> implements Viewer {
    /**
     * Largest diffuse-map dimension for JavaFX. This stops framerate tanking completely.
     */
    private static final int MAX_DIFFUSE_SIZE = 1024;

    private static final Logger log = LoggerFactory.getLogger(ModelPresenter.class);

    @Inject
    public ModelPresenter(ModelView view) {
        super(view);
    }

    @Override
    public boolean canPreview(AssetType type) {
        return type == AssetType.MODEL;
    }

    @Override
    public void setData(Object data) {
        if (data == null) {
            getView().setMeshes(List.of(), null);
            return;
        }

        var model = (Model) data;
        var meshes = mapMeshMaterials(model);
        getView().setMeshes(meshes, model.upAxis());
    }

    @Override
    public String getName() {
        return "Model";
    }

    private List<ModelView.MeshMaterial> mapMeshMaterials(Model model) {
        return model.meshes().stream()
            .map(this::mapMeshMaterial)
            .toList();
    }

    private ModelView.MeshMaterial mapMeshMaterial(Mesh mesh) {
        var triangleMesh = mapMesh(mesh);
        var material = mesh.material().map(this::mapMaterial);
        return new ModelView.MeshMaterial(triangleMesh, material);
    }

    private TriangleMesh mapMesh(Mesh mesh) {

        var result = new TriangleMesh(VertexFormat.POINT_NORMAL_TEXCOORD);
        copyIndices(mesh.indices(), result.getFaces());

        copyPoints(mesh.positions(), result.getPoints());
        copy(mesh.normals().orElseThrow(), result.getNormals());
        copy(mesh.texCoords().getFirst(), result.getTexCoords());
        return result;
    }

    private javafx.scene.paint.Material mapMaterial(Material material) {
        var property = material.properties().stream()
            .filter(prop -> prop.type() == MaterialPropertyType.Albedo)
            .findFirst();

        if (property.isEmpty()) {
            return null;
        }

        try {
            var reference = property.get().reference();
            if (reference == null) {
                return null;
            }

            var texture = reference.supplier().get();

            int mip = chooseMip(texture);
            var converted = texture.convertSurface(mip, 0, TextureFormat.B8G8R8A8_SRGB, true);

            var diffuseMap = new WritableImage(new PixelBuffer<>(
                converted.width(),
                converted.height(),
                ((Bytes.Mutable) converted.surfaces().getFirst().data()).asMutableBuffer(), // Disgusting
                PixelFormat.getByteBgraPreInstance()
            ));

            var result = new PhongMaterial();
            result.setDiffuseMap(diffuseMap);
            return result;
        } catch (IOException e) {
            log.warn("Failed to load texture", e);
            return null;
        }
    }

    private int chooseMip(Texture texture) {
        int lastMip = texture.mipCount() - 1;
        for (int mip = 0; mip < lastMip; mip++) {
            var width = Math.max(1, texture.width() >> mip);
            var height = Math.max(1, texture.height() >> mip);
            if (Math.max(width, height) <= MAX_DIFFUSE_SIZE) {
                return mip;
            }
        }
        return lastMip;
    }

    private void copy(Floats floats, ObservableFloatArray floatArray) {
        floatArray.setAll(floats.toArray());
    }

    private void copyPoints(Floats floats, ObservableFloatArray floatArray) {
        var array = floats.toArray();
        for (var i = 0; i < array.length; i++) {
            array[i] *= 100;
        }
        floatArray.setAll(array);
    }

    private void copyIndices(Ints buffer, ObservableFaceArray faces) {
        var capacity = buffer.length();
        var indices = new int[capacity * 3];
        for (int i = 0, o = 0; i < capacity; i++) {
            var index = buffer.get(i);
            indices[o++] = index;
            indices[o++] = index;
            indices[o++] = index;
        }
        faces.addAll(indices);
    }
}
