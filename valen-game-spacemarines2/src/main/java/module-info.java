module valen.game.spacemarines2 {
    requires valen.core;
    requires org.yaml.snakeyaml;
    requires java.sql;

    opens org.redeye.valen.game.spacemarines2.types;
    opens org.redeye.valen.game.spacemarines2.types.spline;
    opens org.redeye.valen.game.spacemarines2.types.template;
    opens org.redeye.valen.game.spacemarines2.types.scene;
    opens org.redeye.valen.game.spacemarines2.types.lwi;

    provides be.twofold.valen.core.game.GameFactory
        with org.redeye.valen.game.spacemarines2.SpaceMarines2GameFactory;
}
