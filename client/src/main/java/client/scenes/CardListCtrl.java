package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Card;
import commons.CardList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.io.IOException;

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

public class CardListCtrl {

    @FXML
    private VBox cardsContainer;

    @FXML
    private TextField title;
    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private CardList cardList;

    @Inject
    public CardListCtrl(ServerUtils server, MainCtrl mainCtrl, CardList cardList) {
        this.server = server;
        this.mainCtrl = mainCtrl;
        this.cardList = cardList;
    }


    @FXML
    public void initialize() throws IOException {
        title.setText(cardList.title);

        for (Card c : cardList.cards) {
            FXMLLoader cardLoader = new FXMLLoader(getClass().getResource("/client/scenes/Card.fxml"));
            cardLoader.setControllerFactory(g -> new CardCtrl(this.server, this.mainCtrl, c));

            VBox cardNode = cardLoader.load();

            this.cardsContainer.getChildren().add(cardNode);
        }
    }


    @FXML
    public void addCard(){
        mainCtrl.showAddCard(this.cardList);
    }

    public void setTitle(String s) {
        this.title.setText(s);
    }
}
