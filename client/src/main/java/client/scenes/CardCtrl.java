package client.scenes;

import javax.inject.Inject;

import client.utils.ServerUtils;
import com.sun.javafx.scene.input.DragboardHelper;
import commons.Card;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.*;
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
    public VBox cardBox;

    @Inject
    public CardCtrl(ServerUtils server, MainCtrl mainCtrl, Card c, VBox cardBox) {
        this.server = server;
        this.mainCtrl = mainCtrl;
        this.card = c;
        this.cardBox = cardBox;
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


        //DRAG AND DROP HANDLERS

        this.cardBox.setOnDragDetected(e -> {
            this.cardBox.setStyle("-fx-opacity: 0.5");

            System.out.println("DEBUG: Drag start");

            Dragboard db = cardBox.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString(String.valueOf(this.card.id));
            db.setContent(content);
            e.consume();
        });

        this.cardBox.setOnDragDone(e -> {
            if (e.getTransferMode() == TransferMode.MOVE) {

                System.out.println("DEBUG: Drag done - removed card with id " + this.card.id);

            }

            e.consume();
        });

        //END OF DRAG AND DROP HANDLER


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
    public void delete() {
        server.removeCard(this.card.id);
    }




}
