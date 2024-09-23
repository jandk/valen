module valen.game.spacemarines2 {
    requires valen.core;
    requires org.yaml.snakeyaml;
    requires java.sql;

    opens org.redeye.valen.game.spacemarines2.types;

    provides be.twofold.valen.core.game.GameFactory
        with org.redeye.valen.game.spacemarines2.SpaceMarines2GameFactory;
}
