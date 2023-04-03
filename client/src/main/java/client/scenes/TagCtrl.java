package client.scenes;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import javax.inject.Inject;

import client.utils.ServerUtils;
import client.utils.UIUtils;
import commons.Tag;
import jakarta.ws.rs.WebApplicationException;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

public class TagCtrl implements Initializable {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    @FXML
    private TextField name;
    @FXML
    public Button delete;
    public Tag tag;

    @Inject
    public TagCtrl(ServerUtils server, MainCtrl mainCtrl, Tag tag) {
        this.server = server;
        this.mainCtrl = mainCtrl;
        this.tag = tag;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        prepareNameField();
    }

    public void prepareNameField() {
        name.textProperty().addListener((o, oldV, newV) -> {
            if(!Objects.equals(tag.name, newV)) {
                name.setStyle("-fx-text-fill: red;");
            }
        });

        name.setOnKeyPressed(e -> {
            if(e.getCode().equals(KeyCode.ENTER) && name.getStyle().equals("-fx-text-fill: red;")) {
                updateName();
            }
        } );

        name.focusedProperty().addListener((o, oldV, newV) -> {
            if(!newV && name.getStyle().equals("-fx-text-fill: red;")) {
                updateName();
            }
        });
        name.setText(tag.name);
    }

    public void updateName() {
        if(name.getText().isEmpty()) {
            name.setText(tag.name);
            name.setStyle("-fx-text-fill: -fx-col-0;");
            UIUtils.showError("Title should not be empty!");
            return;
        }

        name.setStyle("-fx-text-fill: -fx-col-0;");

        tag.name = name.getText();

        try {
            server.updateTag(tag.id, "name", name.getText());
        } catch (WebApplicationException e) {
            UIUtils.showError(e.getMessage());
        }
    }
}
