package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;

public class BoardSettingsCtrl {
    private final MainCtrl mainCtrl;
    private final ServerUtils server;

    @Inject
    public BoardSettingsCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    public void save() {
        mainCtrl.showBoardOverview();
    }
}
