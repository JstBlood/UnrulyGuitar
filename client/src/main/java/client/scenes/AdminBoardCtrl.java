package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Board;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * This class is the controller of the AddCardList scene,
 * where the user can create a new CardList for the current board.
 */

public class AdminBoardCtrl implements Initializable{
    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private Board b;

    @FXML
    private Label bid;

    @Inject
    public AdminBoardCtrl(ServerUtils server, MainCtrl mainCtrl, Board b) {
        this.mainCtrl = mainCtrl;
        this.server = server;
        this.b = b;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.bid.setText(b.title);
    }

    @FXML
    public void delete() {
        server.deleteBoard(b.key);
        mainCtrl.showBoards();
    }
}
