package be.twofold.valen.reader;

import be.twofold.valen.manager.*;
import dagger.*;
import jakarta.inject.*;

@Singleton
@Component(modules = ResourceReaderModule.class)
public interface ManagerFactory {

    FileManager fileManager();

}
