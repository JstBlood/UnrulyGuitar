package client.scenes;

import client.utils.ServerUtils;
import client.utils.UIUtils;
import com.google.inject.Inject;
import commons.Board;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class BoardsCtrl {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML
    private TextField key;

    @FXML
    private ListView<String> previous;

    @FXML
    private Label listLabel;

    @Inject
    public BoardsCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    public void prepare() {
        if(server.isAdmin()) {
            listLabel.setText("All boards:");
            for(var board : server.getBoards()) {
                previous.getItems().add(board.key);
            }
        } else {
            listLabel.setText("Previously joined boards:");
            for(var board : server.getPrevious()) {
                previous.getItems().add(board.key);
            }
        }


    }

    public void join() {
        if(UIUtils.isNullOrEmpty(key.getText())) {
            UIUtils.showError("The board key must not be empty");
            return;
        }

        try {
            Board recievedBoard = server.joinBoard(key.getText());
            System.out.println("[DEBUG] Received board: " + recievedBoard);
        } catch (NotFoundException e) {
            UIUtils.showError("This board has not been found");
        } catch (Exception e) {
            UIUtils.showError("An unexpected error occurred");
        }
    }

    public void create() {
        System.out.println("[DEBUG] Received board: " + server.createBoard());
    }

    public void list(){
        mainCtrl.showList();
    }

    public void back() {
        mainCtrl.showLogon();
    }
}
