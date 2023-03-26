package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Card;
import commons.CardList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;


import java.net.URL;
import java.util.ResourceBundle;

/**
 * This class is the controller of the CardList scene,
 * which is an overview of the current CardList the user is editing
 * and the user can:
 * 1. edit the list:
 *      - change the list's attributes
 *      - add new cards
 *      - remove cards
 * 2. delete the list
 */

public class CardListCtrl implements Initializable {

    @FXML
    private VBox mainContainer;
    @FXML
    private VBox cardsContainer;
    @FXML
    private TextField title;
    
    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    public CardList cardList;

    @Inject
    public CardListCtrl(ServerUtils server, MainCtrl mainCtrl, CardList cardList) {
        this.server = server;
        this.mainCtrl = mainCtrl;
        this.cardList = cardList;
    }


    @FXML
    @SuppressWarnings("checkstyle:MethodLength")
    public void initialize(URL uri, ResourceBundle rs) {
        title.setText(cardList.title);

        title.setOnKeyPressed(e -> {
            if (e.getCode().equals(KeyCode.ENTER)) {
                editTitle();
            }
        });


        //DRAG AND DROP HANDLERS

        this.mainContainer.setOnDragOver(e -> {

            if (e.getGestureSource() != this.cardsContainer &&
                    e.getDragboard().hasString()) {
                e.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }

            e.consume();
        });

        this.mainContainer.setOnDragEntered(e -> {

            if (e.getGestureSource() != this.mainContainer &&
                    e.getDragboard().hasString()) {

                this.mainContainer.setStyle("-fx-effect: dropshadow(three-pass-box, " +
                        "rgba(255, 255, 255, 0.7), 5, 0.4, 0, 0)");

            }

            e.consume();
        });

        this.mainContainer.setOnDragExited(e -> {

            this.mainContainer.setStyle("-fx-effect: none");

            e.consume();
        });

        this.mainContainer.setOnDragDropped(e -> {
            Dragboard db = e.getDragboard();
            var id = Long.parseLong(db.getString());

            boolean success = false;

            System.out.println("DEBUG: Initialized DragBoard and newCard id");

            if (db.hasString()) {
                Card newCard = server.getCard(id);
                newCard.parentCardList = this.cardList;

                server.removeCard(id);
                server.addCard(newCard);

                this.cardList.cards.add(this.cardList.cards.size(), newCard);

                System.out.println("DEBUG: Removed oldCard from DB using id and " + "added newCard to" +
                        " DB and to current cardList at last index !!TODO: Implement Indexing!!");

                success = true;
            }

            e.setDropCompleted(success);
            e.consume();
        });

        //END OF DRAG AND DROP HANDLERS


        for (Card c : cardList.cards) {
            FXMLLoader cardLoader = new FXMLLoader(getClass().getResource("/client/scenes/Card.fxml"));
            cardLoader.setControllerFactory(g -> new CardCtrl(this.server, this.mainCtrl, c, cardsContainer));

            try {
                VBox cardNode = cardLoader.load();
                this.cardsContainer.getChildren().add(cardNode);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    //TODO: move this into constructor and initialize methods
    public void setup(CardList cardList) {
        this.cardList = cardList;
        title.setText(cardList.title);
    }
    public void editTitle() {
        server.editCardList(cardList.id, "title", title.getText());
    }

    @FXML
    public void deleteCardList() {
        server.deleteCardList(cardList.id);
    }

    @FXML
    public void addCard() {
        this.mainCtrl.showAddCard(this.cardList);
    }

}
