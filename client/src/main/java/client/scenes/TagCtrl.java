package client.scenes;

import java.net.URL;
import java.util.ResourceBundle;
import javax.inject.Inject;

import client.utils.ServerUtils;
import commons.Tag;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

public class TagCtrl implements Initializable {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    @FXML
    private Label tagLabel;
    public Tag tag;

    @Inject
    public TagCtrl(ServerUtils server, MainCtrl mainCtrl, Tag tag) {
        this.server = server;
        this.mainCtrl = mainCtrl;
        this.tag = tag;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        prepareLabel();
    }

    private void prepareLabel() {
        tagLabel.setText(tag.name);
        //TODO: add handlers
    }

    @FXML
    public void delete() {
        server.deleteTag(tag.id);
    }
}
