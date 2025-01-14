import be.twofold.valen.game.fear.*;

module valen.game.fear {
    requires valen.core;
    requires java.xml.crypto;

    provides be.twofold.valen.core.game.GameFactory
        with FearGameFactory;
}