package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Board;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Orientation;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import static javafx.scene.input.KeyCode.ENTER;

public class ShowBoardCtrl implements Initializable {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private Board board;
    @FXML
    private GridPane root;
    @FXML
    private Button addListButton;
    @FXML
    private TextField listNameField;
    @FXML
    private ImageView bracketRight;
    @FXML
    private ImageView settingsButton;
    @FXML
    private Label title;

    @Inject
    public ShowBoardCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    /**
     * @param location  The location used to resolve relative paths for the root object, or
     *                  {@code null} if the location is not known.
     * @param resources The resources used to localize the root object, or {@code null} if
     *                  the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        listNameField.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.getCode() == ENTER) {
                if(listNameField.getText().trim().isEmpty()){
                    listNameField.setStyle("-fx-border-color: red");
                    return;
                }

                addList(listNameField.getText());
            }
        });
    }

    public void editBoard() {

    }

    public void openListNameField() {
        addListButton.setVisible(false);
        listNameField.setVisible(true);
    }

    public void addList(String listName) {
        // adding new things
        Label listTitle = new Label(listName);
        listTitle.getStyleClass().add("text-0");
        listTitle.setMaxWidth(Integer.MAX_VALUE);
        listTitle.setMaxHeight(Integer.MAX_VALUE);

        Separator listSeparator = new Separator();
        listSeparator.setOrientation(Orientation.VERTICAL);
        listSeparator.getStyleClass().add("card-list-separator");
        GridPane.setRowSpan(listSeparator, GridPane.REMAINING);

        Button addCardButton = new Button("+");
        addCardButton.getStyleClass().add("button-1");
        GridPane.setValignment(addCardButton, VPos.CENTER);
        GridPane.setHalignment(addCardButton, HPos.CENTER);

        root.add(listTitle, root.getColumnCount() - 1, 2);
        root.add(addCardButton, root.getColumnCount() - 1, root.getRowCount() - 1);
        root.add(listSeparator, root.getColumnCount(), 2);

        root.getColumnConstraints().add(new ColumnConstraints(10));
        root.getColumnConstraints().add(new ColumnConstraints(200));

        listNameField.setText("");

        addListButton.setVisible(true);
        listNameField.setVisible(false);

        // moving stuff around
        List<Node> l = List.of(addListButton, listNameField, settingsButton, bracketRight);
        l.forEach(n -> GridPane.setColumnIndex(n, GridPane.getColumnIndex(n) + 2));

        GridPane.setColumnSpan(title, GridPane.getColumnSpan(title) + 2);
    }
}
