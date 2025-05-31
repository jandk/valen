package be.twofold.valen.game.idtech.renderparm;

public sealed interface ParmEdit {
    record NoEdit() implements ParmEdit {
    }

    record Bool() implements ParmEdit {
    }

    record Range(Number min, Number max) implements ParmEdit {
    }

    record Color() implements ParmEdit {
    }

    record Srgba() implements ParmEdit {
    }
}
