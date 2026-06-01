module valen.game.doom
{
    requires org.slf4j;
    requires valen.core;

    opens be.twofold.valen.game.doom.readers.image;
    opens be.twofold.valen.game.doom.resources;

    provides be.twofold.valen.core.game.GameFactory
        with be.twofold.valen.game.doom.DoomGameFactory;
}
