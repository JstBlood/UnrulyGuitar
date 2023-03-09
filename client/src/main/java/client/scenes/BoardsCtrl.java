package client.scenes;

import client.utils.ServerUtils;
import client.utils.UIUtils;
import com.google.inject.Inject;
import commons.Board;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class BoardsCtrl {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML
    private TextField bid;

    @Inject
    public BoardsCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    public void prepare() {
        server.connect();
    }

    public void join() {
        Board recv = server.joinBoard(bid.getText());

        if(recv == null)
            UIUtils.showError("This board has not been found");

        System.out.println("[DEBUG] Received board: " + recv);
    }

    public void create() {
        System.out.println("[DEBUG] Received board: " + server.createBoard());
    }

}
