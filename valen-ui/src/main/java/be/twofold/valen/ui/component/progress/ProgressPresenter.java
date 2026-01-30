package be.twofold.valen.ui.component.progress;

import be.twofold.valen.ui.common.*;
import jakarta.inject.*;

public class ProgressPresenter extends AbstractPresenter<ProgressView> implements ProgressView.Listener {
    private Runnable cancelHandler;

    @Inject
    public ProgressPresenter(ProgressController controller) {
        super(controller);
        controller.setListener(this);
    }

    @Override
    public void onCancelClicked() {
        if (cancelHandler != null) {
            cancelHandler.run();
        }
    }

    public void setCancelHandler(Runnable handler) {
        this.cancelHandler = handler;
    }

    public void update(Progress progress) {
        getView().updateProgress(progress);
    }
}
