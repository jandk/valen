package be.twofold.valen.reader;

import be.twofold.valen.reader.binaryfile.*;
import be.twofold.valen.reader.binaryfile.blang.*;
import be.twofold.valen.reader.compfile.*;
import be.twofold.valen.reader.compfile.entities.*;
import be.twofold.valen.reader.file.*;
import be.twofold.valen.reader.file.mapresources.*;
import be.twofold.valen.reader.image.*;
import be.twofold.valen.reader.md6.*;
import be.twofold.valen.reader.md6anim.*;
import be.twofold.valen.reader.md6skl.*;
import be.twofold.valen.reader.model.*;
import dagger.Module;
import dagger.*;
import dagger.multibindings.*;

@Module
abstract class ResourceReaderModule {

    @IntoSet
    @Provides
    static ResourceReader<?> provideBinaryFileReader(BinaryFileReader reader) {
        return reader;
    }

    @IntoSet
    @Provides
    static ResourceReader<?> provideBlangReader(BlangReader reader) {
        return reader;
    }

    @IntoSet
    @Provides
    static ResourceReader<?> provideCompFileReader(CompFileReader reader) {
        return reader;
    }

    @IntoSet
    @Provides
    static ResourceReader<?> provideEntityReader(EntityReader reader) {
        return reader;
    }

    @IntoSet
    @Provides
    static ResourceReader<?> provideFileReader(FileReader reader) {
        return reader;
    }

    @IntoSet
    @Provides
    static ResourceReader<?> provideImageReader(ImageReader reader) {
        return reader;
    }

    @IntoSet
    @Provides
    static ResourceReader<?> provideMapResourcesReader(MapResourcesReader reader) {
        return reader;
    }

    @IntoSet
    @Provides
    static ResourceReader<?> provideMd6AnimReader(Md6AnimReader reader) {
        return reader;
    }

    @IntoSet
    @Provides
    static ResourceReader<?> provideMd6MeshReader(Md6MeshReader reader) {
        return reader;
    }

    @IntoSet
    @Provides
    static ResourceReader<?> provideMd6SklReader(Md6SklReader reader) {
        return reader;
    }

    @IntoSet
    @Provides
    static ResourceReader<?> provideModelReader(ModelReader reader) {
        return reader;
    }

}
