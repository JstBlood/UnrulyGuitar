package client.scenes;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Card;
import commons.CardList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;

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

    @Override
    public void initialize(URL uri, ResourceBundle rs) {
        title.setText(cardList.title);

        title.textProperty().addListener((o, oldV, newV) -> {
            if(!Objects.equals(oldV, newV)) {
                title.setStyle("-fx-text-fill: red;");
            }
        });

        title.setOnKeyPressed(e -> {
            if(e.getCode().equals(KeyCode.ENTER) && title.getStyle().equals("-fx-text-fill: red;")) {
                updateTitle();
            }
        } );

        title.focusedProperty().addListener((o, oldV, newV) -> {
            if(!newV && title.getStyle().equals("-fx-text-fill: red;")) {
                updateTitle();
            }
        });

        for (Card c : cardList.cards) {
            FXMLLoader cardLoader = new FXMLLoader(getClass().getResource("/client/scenes/Card.fxml"));
            cardLoader.setControllerFactory(g -> new CardCtrl(this.server, this.mainCtrl, c));

            try {
                VBox cardNode = cardLoader.load();
                this.cardsContainer.getChildren().add(cardNode);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
    public void updateTitle() {
        title.setStyle("-fx-text-fill: white;");
        cardList.title = title.getText();

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
