package be.twofold.valen.reader;

import be.twofold.valen.reader.binaryfile.*;
import be.twofold.valen.reader.binaryfile.blang.*;
import be.twofold.valen.reader.decl.*;
import be.twofold.valen.reader.decl.renderparm.*;
import be.twofold.valen.reader.file.*;
import be.twofold.valen.reader.file.mapresources.*;
import be.twofold.valen.reader.filecompressed.*;
import be.twofold.valen.reader.filecompressed.entities.*;
import be.twofold.valen.reader.image.*;
import be.twofold.valen.reader.md6anim.*;
import be.twofold.valen.reader.md6skl.*;
import be.twofold.valen.reader.model.*;
import be.twofold.valen.reader.staticinstances.*;
import be.twofold.valen.reader.md6model.*;
import be.twofold.valen.reader.md6skel.*;
import be.twofold.valen.reader.staticmodel.*;
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
    static ResourceReader<?> provideCompFileReader(FileCompressedReader reader) {
        return reader;
    }

    @IntoSet
    @Provides
    static ResourceReader<?> provideDeclReader(DeclReader reader) {
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

//    @IntoSet
//    @Provides
//    static ResourceReader<?> provideMaterialReader(MaterialReader reader) {
//        return reader;
//    }

    @IntoSet
    @Provides
    static ResourceReader<?> provideMd6AnimReader(Md6AnimReader reader) {
        return reader;
    }

    @IntoSet
    @Provides
    static ResourceReader<?> provideMd6MeshReader(Md6ModelReader reader) {
        return reader;
    }

    @IntoSet
    @Provides
    static ResourceReader<?> provideMd6SklReader(Md6SkelReader reader) {
        return reader;
    }

    @IntoSet
    @Provides
    static ResourceReader<?> provideModelReader(StaticModelReader reader) {
        return reader;
    }

    @IntoSet
    @Provides
    static ResourceReader<?> provideRenderParmReader(RenderParmReader reader) {
        return reader;
    }

    @IntoSet
    @Provides
    static ResourceReader<?> provideStaticInstanceReader(StaticInstancesReader reader) {
        return reader;
    }

    @IntoSet
    @Provides
    static ResourceReader<?> provideEntitiesReader(EntityReader reader) {
        return reader;
    }

}
