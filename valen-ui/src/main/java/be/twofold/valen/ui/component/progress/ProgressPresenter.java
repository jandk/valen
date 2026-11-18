package be.twofold.valen.ui.component.progress;

import be.twofold.valen.ui.common.*;
import be.twofold.valen.ui.service.*;
import jakarta.inject.*;

public final class ProgressPresenter extends AbstractPresenter<ProgressView> implements ProgressView.Listener {
    private final ExportService exportService;
    private boolean showing;

    @Inject
    ProgressPresenter(ProgressView view, ExportService exportService, UiThread uiThread) {
        super(view);
        this.exportService = exportService;

        view.setListener(this);
        exportService.progressProperty().subscribe(progress -> uiThread.execute(() -> progressChanged(progress)));
    }

    @Override
    public void onCancelClicked() {
        exportService.cancel();
    }

    private void progressChanged(ExportProgress progress) {
        if (progress == null) {
            if (showing) {
                getView().hide();
                showing = false;
            }
            return;
        }

        getView().updateProgress(progress);
        if (!showing) {
            getView().show();
            showing = true;
        }
    }
}
