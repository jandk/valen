package be.twofold.valen.ui.component.modelviewer;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.geometry.Mesh;
import be.twofold.valen.core.material.*;
import be.twofold.valen.core.material.Material;
import be.twofold.valen.core.texture.*;
import be.twofold.valen.core.util.collect.*;
import be.twofold.valen.ui.common.*;
import be.twofold.valen.ui.component.*;
import jakarta.inject.*;
import javafx.collections.*;
import javafx.scene.image.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;

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
        var pointBuffer = mesh.getPositions();
        var normalBuffer = mesh.getNormals().orElseThrow();
        var texCoordBuffer = mesh.getTexCoords().orElseThrow();

        var result = new TriangleMesh(VertexFormat.POINT_NORMAL_TEXCOORD);
        copyPoints(pointBuffer, result.getPoints());
        copy(normalBuffer, result.getNormals());
        copy(texCoordBuffer, result.getTexCoords());
        copyIndices(mesh.indexBuffer(), result.getFaces());
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

    private void copy(VertexBuffer<Floats> buffer, ObservableFloatArray floatArray) {
        var floatBuffer = buffer.buffer();
        var array = new float[floatBuffer.size()];
        floatBuffer.copyTo(MutableFloats.wrap(array), 0);
        floatArray.setAll(array);
    }

    private void copyPoints(VertexBuffer<Floats> buffer, ObservableFloatArray floatArray) {
        var floatBuffer = buffer.buffer();
        var array = new float[floatBuffer.size()];
        floatBuffer.copyTo(MutableFloats.wrap(array), 0);

        for (var i = 0; i < array.length; i++) {
            array[i] *= 100;
        }
        floatArray.setAll(array);
    }

    private void copyIndices(VertexBuffer<?> buffer, ObservableFaceArray faces) {
        switch (buffer.buffer()) {
            case MutableBytes _ -> throw new UnsupportedOperationException("Bytes not supported yet");
            case MutableShorts shorts -> {
                var capacity = shorts.size();
                var indices = new int[capacity * 3];
                for (int i = 0, o = 0; i < capacity; i++) {
                    var index = Short.toUnsignedInt(shorts.getShort(i));
                    indices[o++] = index;
                    indices[o++] = index;
                    indices[o++] = index;
                }
                faces.addAll(indices);
            }
            case MutableInts _ -> throw new UnsupportedOperationException("Ints not supported yet");
            default -> throw new UnsupportedOperationException("Unexpected type: " + buffer.buffer().getClass());
        }
    }
}
