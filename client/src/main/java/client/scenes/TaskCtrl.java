package client.scenes;

import client.utils.ServerUtils;
import client.utils.UIUtils;
import commons.Task;
import jakarta.ws.rs.WebApplicationException;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.input.*;

import javax.inject.Inject;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class TaskCtrl implements Initializable {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML
    private TextField title;
    @FXML
    private CheckBox isDone;

    public Task t;

    @Inject
    public TaskCtrl(ServerUtils server, MainCtrl mainCtrl, Task t) {
        this.server = server;
        this.mainCtrl = mainCtrl;
        this.t = t;
    }

    @Override
    public void initialize(URL location, ResourceBundle rs) {
        title.textProperty().addListener((o, oldV, newV) -> {
            if(!Objects.equals(t.title, newV)) {
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

        isDone.selectedProperty().addListener((o, oldV, newV) -> {
            server.updateTask(t.id, "isDone", isDone.isSelected());
        });

        this.title.setText(t.title);
        this.isDone.setSelected(t.isDone);
    }

    public void updateTitle() {
        if(title.getText().isEmpty()) {
            title.setText(t.title);
            title.setStyle("-fx-text-fill: black;");
            UIUtils.showError("Title should not be empty!");
            return;
        }

        title.setStyle("-fx-text-fill: black;");

        t.title = title.getText();

        try {
            server.updateTask(t.id, "title", title.getText());
        } catch (WebApplicationException e) {
            UIUtils.showError(e.getMessage());
        }
    }

    @FXML
    public void delete() {
        server.deleteTask(t.id);
    }

}
