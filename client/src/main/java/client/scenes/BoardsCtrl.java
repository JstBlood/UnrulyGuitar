package client.scenes;

import client.utils.ServerUtils;
import client.utils.UIUtils;
import com.google.inject.Inject;
import commons.Board;
import jakarta.ws.rs.BadRequestException;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class BoardsCtrl {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML
    private TextField key;

    @Inject
    public BoardsCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    public void prepare() {
        server.connect();
    }

    public void join() {
        if(UIUtils.isNullOrEmpty(key.getText())) {
            UIUtils.showError("The board key must not be empty");
            return;
        }

        try {
            Board recievedBoard = server.joinBoard(key.getText());
            System.out.println("[DEBUG] Received board: " + recievedBoard);
        } catch (BadRequestException e) {
            UIUtils.showError("This board has not been found");
        } catch (Exception e) {
            UIUtils.showError("An unexpected error occurred");
        }
    }

    public void create() {
        System.out.println("[DEBUG] Received board: " + server.createBoard());
    }

    public void back() {

    }

}
