package client.scenes;

import client.utils.ServerUtils;
import commons.Card;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import javax.inject.Inject;

public class CardCtrl {
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

    @FXML
    public void initialize() {
        this.title.setText(card.title);

        this.title.textProperty().addListener((o, oldV, newV) -> {
            this.card.title = newV;

            mainCtrl.silenceOnce();
            server.editCardTitle(card.id, newV);
        });

        this.description.setText(card.description);
        this.description.setPrefRowCount((int) card.description.lines().count());
    }

    @FXML
    public void remove() {
        server.removeCard(this.card.id);
    }
}
