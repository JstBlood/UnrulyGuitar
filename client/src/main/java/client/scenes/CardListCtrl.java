package client.scenes;

import java.net.URL;
import java.util.ResourceBundle;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.CardList;
import javafx.fxml.FXML;
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
    public CardListCtrl(ServerUtils server, MainCtrl mainCtrl){
        this.server = server;
        this.mainCtrl = mainCtrl;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        title.setOnKeyPressed(e -> {
            if(e.getCode().equals(KeyCode.ENTER)) {
                editTitle();
            }
        });
    }

    @FXML
    public void addCard(){
        mainCtrl.showAddCard(this.cardList);
    }

    @FXML
    public void removeCard(){
        // cardContainer used to be a ListView, but a ListView can only contain Strings,
        // while the cards are VBoxes, so I had to refactor it.
        // Unfortunately, VBoxes do not have SelectionModels, so this code is deprecated.
        // TODO: figure out a different way to remove cards.
//        int id = listView.getSelectionModel().getSelectedIndex();
//        listView.getItems().remove(id);
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

    public void addCardToContainer(VBox cardNode){
        this.cardsContainer.getChildren().add(cardNode);
    }

}
