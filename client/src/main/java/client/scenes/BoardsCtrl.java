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
        if(bid.getText() == "" || bid.getText() == null) {
            UIUtils.showError("The board id mustn't be empty");
            return;
        }

        try {
            Board recv = server.joinBoard(bid.getText());
            System.out.println("[DEBUG] Received board: " + recv);
        } catch (BadRequestException e) {
            UIUtils.showError("This board has not been found");
        } catch (Exception e) {
            UIUtils.showError("An unexpected error occurred");
        }
    }

    public void create() {
        System.out.println("[DEBUG] Received board: " + server.createBoard());
    }

}
