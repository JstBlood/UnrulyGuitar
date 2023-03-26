package client.scenes;

import javax.inject.Inject;

import client.utils.ServerUtils;
import commons.Card;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.TransferMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class CardCtrl implements Initializable {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    private Card card;

    @FXML
    private TextField title;
    @FXML
    private TextArea description;
    @FXML
    private VBox cardBox;

    @Inject
    public CardCtrl(ServerUtils server, MainCtrl mainCtrl, Card c) {
        this.server = server;
        this.mainCtrl = mainCtrl;
        this.card = c;
    }

    @FXML
    public void initialize(URL location, ResourceBundle rs) {
        this.title.setText(card.title);

        this.title.focusedProperty().addListener((o, oldV, newV) -> {
            if(newV == false)
                updateTitle();
        });

        this.title.textProperty().addListener((o, oldV, newV) -> {
            if(oldV != newV)
                this.title.setStyle("-fx-text-fill: red;");
        });

        this.description.setText(card.description);
        this.description.setPrefRowCount((int) card.description.lines().count());
    }

    @FXML
    public void remove() {
        server.removeCard(this.card.id);
    }

    public void updateTitle() {
        this.title.setStyle("-fx-text-fill: white;");
        this.card.title = title.getText();

        server.editCardTitle(card.id, title.getText());
    }

    @FXML
    public void onDragDetected() {
//        var parentList = this.card.parentCardList;
//        var parentBoard = parentList.parentBoard;
//
//        parentList.removeCard(this.card);
//        server.removeCard(this.card.id);
//        server.forceRefresh(parentBoard.key);
    }

    @FXML
    public void delete() {
        server.removeCard(this.card.id);
    }

    @FXML
    public void onDragExited() {
//        var parentList = this.card.parentCardList;
//        var parentBoard = parentList.parentBoard;
//
//        Card temp = this.card;
//
//        parentList.removeCard(this.card);
//        server.removeCard(this.card.id);
//        server.forceRefresh(parentBoard.key);
    }

}
