package client.scenes;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import javax.inject.Inject;

import client.utils.ServerUtils;
import client.utils.UIUtils;
import commons.Task;
import jakarta.ws.rs.WebApplicationException;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

public class TaskCtrl implements Initializable {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML
    public TextField title;
    @FXML
    public CheckBox isDone;

    public Task t;

    @Inject
    public TaskCtrl(ServerUtils server, MainCtrl mainCtrl, Task t) {
        this.server = server;
        this.mainCtrl = mainCtrl;
        this.t = t;
    }

    @Override
    public void initialize(URL location, ResourceBundle rs) {
        prepareTitle();
        prepareIsDone();
    }

    private void prepareTitle() {
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
        title.setText(t.title);
    }

    private void prepareIsDone() {
        isDone.setSelected(t.isDone);
        isDone.selectedProperty().addListener((o, oldV, newV) -> {
            server.updateTask(t.id, "isDone", isDone.isSelected());
        });
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
    public void shiftUp() {
        if(t.index > 0) {
            Task other = t.parentCard.tasks.get(t.index - 1);
            server.updateTask(t.id, "index", t.index - 1);
            server.updateTask(other.id, "index", other.index + 1);
        }
    }

    @FXML
    public void shiftDown() {
        if(t.index < t.parentCard.tasks.size() - 1) {
            Task other = t.parentCard.tasks.get(t.index + 1);
            server.updateTask(t.id, "index", t.index + 1);
            server.updateTask(other.id, "index", other.index - 1);
        }
    }

    @FXML
    public void delete() {
        server.deleteTask(t.id);
    }

}
