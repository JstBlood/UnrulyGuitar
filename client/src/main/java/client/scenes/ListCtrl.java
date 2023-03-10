package client.scenes;

import com.google.inject.Inject;
import javafx.fxml.FXML;

import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TitledPane;

public class ListCtrl {
    @FXML
    public ListView<String> listView;
    private int i=0;
    @Inject
    public ListCtrl(ListView<String> listView){
        this.listView=listView;
    }
    @FXML
    public void addTask(){
        /*
        new TitledPane("Dummy Card "+i,new Label("The subtasks of the card should be here"))
        We could display the tasks like this, but the selection mode works poorly with it
        */
        listView.getItems().add("Dummy Card"+i);
        i++;
    }
    @FXML
    public void removeTask(){
        int id=listView.getSelectionModel().getSelectedIndex();
        listView.getItems().remove(id);
    }
}
