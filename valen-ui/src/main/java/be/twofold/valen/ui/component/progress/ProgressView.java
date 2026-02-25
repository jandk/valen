package be.twofold.valen.ui.component.progress;

import be.twofold.valen.ui.common.*;

public interface ProgressView extends View<ProgressView.Listener> {

    void updateProgress(Progress progress);

    interface Listener extends View.Listener {
        void onCancelClicked();
    }

}
