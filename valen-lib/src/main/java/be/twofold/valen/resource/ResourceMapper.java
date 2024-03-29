package be.twofold.valen.resource;

import be.twofold.valen.reader.resource.*;

import java.time.*;
import java.util.*;

public final class ResourceMapper {
    public List<Resource> map(Resources resources) {
        return resources.entries().stream()
            .map(entry -> map(resources, entry))
            .toList();
    }

    private Resource map(Resources resources, ResourcesEntry entry) {
        var type = resources.pathStrings().get(resources.pathStringIndex()[entry.strings()]);
        var name = resources.pathStrings().get(resources.pathStringIndex()[entry.strings() + 1]);
        var dependencies = mapDependencies(resources, entry);

        return new Resource(
            new ResourceName(name),
            ResourceType.fromName(type),
            ResourceVariation.fromValue(entry.variation()),
            Instant.ofEpochMilli(entry.generationTimeStamp() / 1000),
            entry.dataOffset(),
            entry.dataSize(),
            entry.uncompressedSize(),
            entry.defaultHash(),
            dependencies
        );
    }

    private List<ResourceDependency> mapDependencies(Resources resources, ResourcesEntry entry) {
        return Arrays.stream(resources.dependencyIndex(), entry.depIndices(), entry.depIndices() + entry.numDependencies())
            .mapToObj(index -> mapDependency(resources, resources.dependencies().get(index)))
            .toList();
    }

    private ResourceDependency mapDependency(Resources resources, ResourcesDependency dependency) {
        var type = resources.pathStrings().get(dependency.type());
        var name = resources.pathStrings().get(dependency.name());

        return new ResourceDependency(
            new ResourceName(name),
            ResourceType.fromName(type)
        );
    }
}
