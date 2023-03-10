package client.scenes;

import com.google.inject.Inject;
import javafx.fxml.FXML;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TitledPane;

public class ListCtrl {
    @FXML
    public ListView<TitledPane> listView;
    private int i=0;
    @Inject
    public ListCtrl(ListView<TitledPane> listView){
        this.listView=listView;
    }
    public void click(){
        listView.getItems().add(new TitledPane("Dummy Card "+i,new Label("The subtasks of the card should be here")));
        i++;
    }
}
