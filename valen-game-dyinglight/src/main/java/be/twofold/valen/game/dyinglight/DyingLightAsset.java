package be.twofold.valen.game.dyinglight;

import be.twofold.valen.core.game.*;
import be.twofold.valen.game.dyinglight.reader.rpack.*;

import java.util.*;

public record DyingLightAsset(
    DyingLightAssetID id,
    RDPFile file,
    List<RDPPart> parts,
    List<ResourceType> sectionTypes
) implements Asset {
    @Override
    public AssetType type() {
        return switch (id.type()) {
            case Mesh -> AssetType.MODEL;
            case Texture -> AssetType.TEXTURE;
            default -> AssetType.RAW;
        };
    }

    public boolean hasSection(ResourceType type) {
        return sectionTypes.contains(type);
    }

    public long sectionOffset(ResourceType type) {
        long totalOffset = 0;
        for (int i = 0; i < parts.size(); i++) {
            final RDPPart part = parts.get(i);
            final ResourceType sectionType = sectionTypes.get(i);
            if (sectionType == type) {
                return totalOffset;
            }
            totalOffset += part.size();
        }
        return -1;
    }

    public long sectionSize(ResourceType type) {
        for (int i = 0; i < parts.size(); i++) {
            final RDPPart part = parts.get(i);
            final ResourceType sectionType = sectionTypes.get(i);
            if (sectionType == type) {
                return part.size();
            }
        }
        return -1;
    }

    @Override
    public int size() {
        return parts.stream().mapToInt(RDPPart::size).sum();
    }

    @Override
    public Map<String, Object> properties() {
        return Map.of(
            "Type", file.type()
        );
    }
}
