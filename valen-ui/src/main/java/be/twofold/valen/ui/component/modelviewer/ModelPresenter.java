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
import wtf.reversed.toolbox.collect.*;

import java.io.*;
import java.nio.*;
import java.util.*;

public final class ModelPresenter extends AbstractFXPresenter<ModelView> implements Viewer {
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
        if (mesh.normals().isPresent()) {
            copy(mesh.normals().get(), result.getNormals());
        } else {
            for (int i = 0; i < result.getPoints().size() / 3; i++) {
                result.getNormals().addAll(0.0f, 0.0f, 1.0f);
            }
        }

        if (!mesh.texCoords().isEmpty()) {
            copy(mesh.texCoords().getFirst(), result.getTexCoords());
        } else {
            for (int i = 0; i < result.getPoints().size() / 3; i++) {
                result.getTexCoords().addAll(0.0f, 0.0f);
            }
        }
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

            // I have to limit this, because the performance absolutely tanks, not sure why yet...
            var surface = texture.surfaces().stream()
                // .filter(s -> s.width() <= 1024 && s.height() <= 1024)
                .skip(2)
                .findFirst();
            if (surface.isEmpty()) {
                return null;
            }

            var converted = Texture.fromSurface(surface.get(), texture.format())
                .convert(TextureFormat.B8G8R8A8_UNORM, true);

            var pixelBuffer = new PixelBuffer<>(
                converted.width(),
                converted.height(),
                ByteBuffer.wrap(converted.surfaces().getFirst().data()),
                PixelFormat.getByteBgraPreInstance()
            );

            var image = new WritableImage(pixelBuffer);
            var result = new PhongMaterial();
            result.setDiffuseMap(image);
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void copy(Floats floats, ObservableFloatArray floatArray) {
        var array = new float[floats.length()];
        floats.copyTo(Floats.Mutable.wrap(array), 0);
        floatArray.setAll(array);
    }

    private void copyPoints(Floats floats, ObservableFloatArray floatArray) {
        var array = new float[floats.length()];
        floats.copyTo(Floats.Mutable.wrap(array), 0);

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
