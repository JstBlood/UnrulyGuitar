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
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

/**
 * This is the controller for the Tag scene which represents a tag.
 */
public class TagCtrl implements Initializable {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    @FXML
    public TextField name;
    @FXML
    public Button delete;

    @FXML
    public ColorPicker backgroundColor;

    @FXML
    public ColorPicker foregroundColor;

    public Tag tag;

    /**
     * Create a tag object.
     * @param server The server connection.
     * @param mainCtrl The main (root) controller.
     * @param tag The initial tag object.
     */
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

    /**
     * Add listeners for the name field.
     */
    public void prepareNameField() {
        name.setText(tag.name);
        delete.setStyle("-fx-text-fill: " + tag.colors.foreground + ";" +
                "-fx-background-color: " + tag.colors.background + ";");
        name.setStyle("-fx-text-fill: " + tag.colors.foreground + ";" +
                "-fx-background-color: " + tag.colors.background + ";");

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


        if(backgroundColor != null)
            backgroundColor.setValue(Color.valueOf(tag.colors.background));
        if(backgroundColor != null)
            foregroundColor.setValue(Color.valueOf(tag.colors.foreground));
    }

    /**
     * We update the name of the tag with the function when we receive an update from the server.
     */
    public void updateName() {
        name.setStyle("-fx-text-fill: " + tag.colors.foreground + ";");

        if(name.getText().isEmpty()) {
            name.setText(tag.name);
            UIUtils.showError("Title should not be empty!");
            return;
        }

        tag.name = name.getText();

        try {
            server.updateTag(tag.id, "name", name.getText());
        } catch (WebApplicationException e) {
            UIUtils.showError(e.getMessage());
        }
    }

    /**
     * This function is called when the user changes the tags' foreground color.
     */
    @FXML
    public void foregroundChange() {
        server.updateTag(tag.id, "foreground",
                "#" + foregroundColor.getValue().toString().substring(2));
    }

    /**
     * This function is called when the user changes the tags' background color.
     */
    @FXML
    public void backgroundChange() {
        server.updateTag(tag.id, "background",
                "#" + backgroundColor.getValue().toString().substring(2));

    }
}
