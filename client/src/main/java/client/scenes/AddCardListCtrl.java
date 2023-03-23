package client.scenes;

import client.utils.ServerUtils;
import client.utils.UIUtils;
import com.google.inject.Inject;
import commons.Board;
import commons.CardList;
import jakarta.ws.rs.WebApplicationException;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * This class is the controller of the AddCardList scene,
 * where the user can create a new CardList for the current board.
 */

public class AddCardListCtrl implements Initializable{
    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    @FXML
    private TextField title;

    private Board parentBoard;

    @Inject
    public AddCardListCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.title.addEventHandler(KeyEvent.KEY_PRESSED, this::keyPressed);
    }

    public void ok() {
        try {
            server.addCardList(getCardList());
        } catch (WebApplicationException e) {
            UIUtils.showError(e.getMessage());
        }

        clearFields();
    }

    private CardList getCardList() {
        return new CardList(title.getText(), parentBoard);
    }

    public void cancel() {
        clearFields();
    }

    private void clearFields() {
        title.clear();
    }

    public void keyPressed(KeyEvent e) {
        switch (e.getCode()) {
            case ENTER:
                ok();
                break;
            case ESCAPE:
                cancel();
                break;
            default:
                break;
        }
    }

    public void setParentBoard(Board parentBoard) {
        this.parentBoard = parentBoard;
    }
}
