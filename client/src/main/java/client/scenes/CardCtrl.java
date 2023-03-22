package client.scenes;

import client.utils.ServerUtils;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import javax.inject.Inject;

public class CardCtrl {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML
    private TextField title;
    @FXML
    private TextArea description;

    @Inject
    public CardCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }

    public void setTitle(String s){
        this.title.setText(s);
    }

    public void setDescription(String s){
        this.description.setText(s);
        this.description.setPrefRowCount((int) s.lines().count());
    }
}
