package client.scenes;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import javax.inject.Inject;

import client.utils.ServerUtils;
import commons.Card;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

public class CardCtrl implements Initializable {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    private Card card;

    @FXML
    private TextField title;
    @FXML
    private TextArea description;

    @Inject
    public CardCtrl(ServerUtils server, MainCtrl mainCtrl, Card c) {
        this.server = server;
        this.mainCtrl = mainCtrl;
        this.card = c;
    }

    @Override
    public void initialize(URL location, ResourceBundle rs) {
        title.setText(card.title);

        title.textProperty().addListener((o, oldV, newV) -> {
            if(!Objects.equals(oldV, newV)) {
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

        description.setText(card.description);
        description.setPrefRowCount((int) card.description.lines().count());
    }

    @FXML
    public void remove() {
        server.removeCard(this.card.id);
    }

    public void updateTitle() {
        title.setStyle("-fx-text-fill: white;");
        card.title = title.getText();

        server.editCardTitle(card.id, title.getText());
    }
}
