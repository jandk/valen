package be.twofold.valen.ui;

import javax.inject.*;

public class MainPresenter {
    private final MainView view;

    @Inject
    public MainPresenter(MainView view) {
        this.view = view;
    }

    public void show() {
        view.show();
    }
}
