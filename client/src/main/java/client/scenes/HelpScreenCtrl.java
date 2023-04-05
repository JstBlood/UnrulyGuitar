package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import javafx.fxml.FXML;

public class HelpScreenCtrl {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private String prevScene;
    @Inject
    public HelpScreenCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }

    @FXML
    public void back() {
        switch (prevScene) {
            case "logon" :
                mainCtrl.showLogon();
                break;
            case "boards" :
                mainCtrl.showBoards();
                break;
            case "boardOverview" :
                mainCtrl.showBoardOverview();
                break;
            case "boardSettings" :
                mainCtrl.showBoardSettings();
                break;
        }
    }

    public void setPrevScene(String prevScene) {
        this.prevScene = prevScene;
    }
}
