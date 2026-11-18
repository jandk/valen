package be.twofold.valen.ui.component.progress;

import be.twofold.valen.ui.common.*;
import be.twofold.valen.ui.service.*;

public interface ProgressView extends View<ProgressView.Listener> {

    void updateProgress(ExportProgress progress);

    void show();

    void hide();

    interface Listener extends View.Listener {
        void onCancelClicked();
    }

}
