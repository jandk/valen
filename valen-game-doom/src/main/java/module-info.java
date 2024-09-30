module valen.game.doom
{
    requires valen.core;

    provides be.twofold.valen.core.game.GameFactory
        with be.twofold.valen.game.doom.DoomGameFactory;
}
