package client.scenes;

import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

/**
 * This class is the controller of the CardList scene,
 * which is an overview of the current CardList the user is editing
 * (will probably be accessed by an "Edit" button the the CardList)
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
    private int i=0;
    @Inject
    public CardListCtrl(ListView<String> listView){
        this.listView=listView;
    }
    @FXML
    public void addCard(){
        /*
        new TitledPane("Dummy Card "+i,new Label("The subtasks of the card should be here"))
        We could display the tasks like this, but the selection mode works poorly with it
        */
        listView.getItems().add("Dummy Card"+i);
        i++;
    }
    @FXML
    public void removeCard(){
        int id=listView.getSelectionModel().getSelectedIndex();
        listView.getItems().remove(id);
    }
}
