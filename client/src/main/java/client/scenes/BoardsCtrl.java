package client.scenes;

import java.io.IOException;
import java.util.Random;

import client.utils.ServerUtils;
import client.utils.UIUtils;
import com.google.inject.Inject;
import commons.Board;
import jakarta.ws.rs.NotFoundException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

/**
 * This class is the controller of the Boards scene,
 * which is basically a menu where the user can either:
 * 1. join a new board
 * 2. create a new board
 * 3. choose a board from the list of previously joined boards
 */

public class BoardsCtrl {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML
    private TextField key;

    @FXML
    private ListView<Parent> previous;

    @FXML
    private Label listLabel;

    @Inject
    public BoardsCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    public void prepare() {
        previous.getItems().clear();

        if(mainCtrl.accessStore().isAdmin()) {
            listLabel.setText("All boards:");
            for(var board : server.getBoards()) {
                addToPreviousList(board, true);
            }
        } else {
            listLabel.setText("Previously joined boards:");
            for(var board : server.getPrevious()) {
                addToPreviousList(board, false);
            }
        }
    }

    private void addToPreviousList(Board board, boolean isAdmin) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/client/scenes/" +
                    (isAdmin ? "AdminBoard.fxml" :  "NonAdminBoard.fxml")));
            loader.setControllerFactory(c -> new AdminBoardCtrl(server, mainCtrl, board));
            Parent root = loader.load();
            root.setUserData(board);
            previous.getItems().add(root);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void join() {
        if(UIUtils.isNullOrEmpty(key.getText())) {
            UIUtils.showError("The board key must not be empty");
            return;
        }

        Board receivedBoard = null;
        try {
            receivedBoard = server.joinBoard(key.getText());
        } catch (NotFoundException e) {
            UIUtils.showError("This board has not been found");
        } catch (Exception e) {
            UIUtils.showError("An unexpected error occurred");
        }

        clearFields();

        mainCtrl.setupBoardOverview(receivedBoard);
        mainCtrl.showBoardOverview();
    }

    public void create() {
        Random rng = new Random();
        Board created = new Board(Long.toString(rng.nextLong()), "New board");

        mainCtrl.setupBoardOverview(server.addBoard(created));
        mainCtrl.showBoardOverview();
        System.out.println("[DEBUG] Received board: " + created);
    }

    public void clearFields() {
        this.key.clear();
    }

    public void fillIn() {
        key.setText(((Board)previous.getSelectionModel().getSelectedItem().getUserData()).key);
    }

    public void back() {
        mainCtrl.showLogon();
    }
}
