module valen.game.doom
{
    requires org.slf4j;
    requires valen.core;

    provides be.twofold.valen.core.game.GameFactory
        with be.twofold.valen.game.doom.DoomGameFactory;
}
