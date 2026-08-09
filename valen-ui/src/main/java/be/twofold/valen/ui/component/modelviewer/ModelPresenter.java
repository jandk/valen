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
    public Object decode(Object data) {
        if (data == null) {
            return null;
        }

        var model = (Model) data;
        return new ModelPayload(mapMeshMaterials(model), model.upAxis());
    }

    @Override
    public void display(Object payload) {
        if (payload == null) {
            getView().setMeshes(new ModelPayload(List.of(), null));
            return;
        }

        getView().setMeshes((ModelPayload) payload);
    }

    @Override
    public String getName() {
        return "Model";
    }

    private List<ModelPayload.MeshMaterial> mapMeshMaterials(Model model) {
        return model.meshes().stream()
            .map(this::mapMeshMaterial)
            .toList();
    }

    private ModelPayload.MeshMaterial mapMeshMaterial(Mesh mesh) {
        var diffuse = mesh.material().flatMap(this::loadDiffuse);
        return new ModelPayload.MeshMaterial(mesh, diffuse);
    }

    private Optional<DecodedImage> loadDiffuse(Material material) {
        var property = material.properties().stream()
            .filter(prop -> prop.type() == MaterialPropertyType.Albedo)
            .findFirst();

        if (property.isEmpty()) {
            return Optional.empty();
        }

        try {
            var reference = property.get().reference();
            if (reference == null) {
                return Optional.empty();
            }

            var texture = reference.supplier().get();

            int mip = chooseMip(texture);
            var converted = texture.convertSurface(mip, 0, TextureFormat.B8G8R8A8_SRGB, true);

            return Optional.of(new DecodedImage(
                converted.width(),
                converted.height(),
                (Bytes.Mutable) converted.surfaces().getFirst().data()
            ));
        } catch (IOException e) {
            log.warn("Failed to load texture", e);
            return Optional.empty();
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
}
