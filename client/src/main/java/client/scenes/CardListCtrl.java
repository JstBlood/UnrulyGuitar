package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

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
    public ListView<String> listView;
    @FXML
    public TextField title;

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @Inject
    public CardListCtrl(ServerUtils server, MainCtrl mainCtrl, ListView<String> listView){
        this.server = server;
        this.mainCtrl = mainCtrl;
        this.listView=listView;
    }

    @FXML
    public void addCard(){
        mainCtrl.showAddCard();
    }

    @FXML
    public void removeCard(){
        int id = listView.getSelectionModel().getSelectedIndex();
        listView.getItems().remove(id);
    }
}
