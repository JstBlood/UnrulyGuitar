package client.scenes;

import java.util.Random;

import client.utils.ServerUtils;
import client.utils.UIUtils;
import com.google.inject.Inject;
import commons.Board;
import jakarta.ws.rs.BadRequestException;
import javafx.fxml.FXML;
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
            Board receivedBoard = server.joinBoard(key.getText());

            System.out.println("[DEBUG] Received board: " + receivedBoard);

            mainCtrl.setCurrentBoard(receivedBoard);
            mainCtrl.showBoardOverview();

        } catch (BadRequestException e) {
            UIUtils.showError("This board has not been found");
        } catch (Exception e) {
            UIUtils.showError("An unexpected error occurred");
        }
    }

    public void create() {
        Random rng = new Random();
        Board created = new Board(Long.toString(rng.nextLong()), "New board");
        server.addBoard(created);
        System.out.println("[DEBUG] Received board: " + created);
    }

}
