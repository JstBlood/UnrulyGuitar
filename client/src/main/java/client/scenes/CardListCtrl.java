package client.scenes;

import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;
import java.util.ResourceBundle;

import client.utils.ServerUtils;
import client.utils.UIUtils;
import com.google.inject.Inject;
import commons.Card;
import commons.CardList;
import jakarta.ws.rs.WebApplicationException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;

/**
 * This class is the controller of the CardList scene,
 * which is an overview of the current CardList the user is editing
 * and the user can:
 * 1. edit the list:
 *      - change the list's attributes
 *      - add new cards
 *      - remove cards
 * 2. delete the list
 */

public class CardListCtrl implements Initializable {

    @FXML
    private VBox mainContainer;
    @FXML
    private VBox cardsContainer;
    @FXML
    private TextField title;

    @FXML
    private TextField cardName;
    
    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    public CardList cardList;

    @Inject
    public CardListCtrl(ServerUtils server, MainCtrl mainCtrl, CardList cardList) {
        this.server = server;
        this.mainCtrl = mainCtrl;
        this.cardList = cardList;
    }


    @FXML
    @Override
    public void initialize(URL uri, ResourceBundle rs) {
        this.mainContainer.setOnDragOver(e -> {

            if (e.getGestureSource() != this.cardsContainer &&
                    e.getDragboard().hasString()) {
                e.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }

            e.consume();
        });

        this.mainContainer.setOnDragEntered(e -> {
            if (e.getGestureSource() != this.mainContainer &&
                    e.getDragboard().hasString()) {

                this.mainContainer.setStyle("-fx-effect: dropshadow(three-pass-box, " +
                        "rgba(255, 255, 255, 0.7), 5, 0.4, 0, 0)");

            }

            e.consume();
        });

        this.mainContainer.setOnDragExited(e -> {
            this.mainContainer.setStyle("-fx-effect: none");
            e.consume();
        });

        this.mainContainer.setOnDragDropped(e -> {

            handleDragEvent(e);
        });

        this.mainContainer.setOnDragDone(e -> {
            e.consume();
        });
        //END OF DRAG AND DROP HANDLERS

        prepare();
        prepareTitleField();
        showCards();
    }

    public void showCards() {
        var cardsOrdered = new ArrayList<>(cardList.cards);
        cardsOrdered.sort(Comparator.comparingInt(card -> card.index));

        for (Card c : cardsOrdered) {
            FXMLLoader cardLoader = new FXMLLoader(getClass().getResource("/client/scenes/Card.fxml"));
            cardLoader.setControllerFactory(g -> new CardCtrl(this.server, this.mainCtrl, c, cardsContainer));

            try {
                VBox cardNode = cardLoader.load();
                cardsContainer.getChildren().add(cardNode);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void prepare() {
        server.registerForUpdates(c -> {
            Platform.runLater(() -> {
                server.forceRefresh(cardList.parentBoard.key);
            });
        });
    }

    private void prepareTitleField() {
        title.setText(cardList.title);

        title.textProperty().addListener((o, oldV, newV) -> {
            if(!Objects.equals(cardList.title, newV)) {
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
    }

    private Card generateCard() {
        return new Card(cardName.getText(), "", this.cardList);
    }

    @FXML
    public void cardAdd() {
        if(cardName.getText() == "") {
            UIUtils.showError("Card name cannot be empty");
            return;
        }

        try {
            Card newCard = generateCard();
            server.addCard(newCard);
        } catch (WebApplicationException e) {
            UIUtils.showError(e.getMessage());
        }
    }

    private void handleDragEvent(DragEvent e) {
        Dragboard db = e.getDragboard();
        var id = Long.parseLong(db.getString());
        boolean success = false;

        System.out.println("DEBUG: Initialized DragBoard and newCard id");

        if (db.hasString()) {
            var node = (VBox) e.getGestureSource();
            moveToList(node);

            System.out.println("DEBUG: Removed oldCard from DB using id and " + "added newCard to" +
                    " DB and to current cardList at last index");

            success = true;
        }

        e.setDropCompleted(success);
        e.consume();
    }

    public void updateTitle() {
        if (title.getText().isEmpty()) {
            title.setText(cardList.title);
            title.setStyle("-fx-text-fill: -fx-col-0;");
            UIUtils.showError("Title should not be empty!");
            return;
        }
        cardList.title = title.getText();

        title.setStyle("-fx-text-fill: -fx-col-0;");

        try {
            server.updateCardList(cardList.id, "title", title.getText());
        } catch (RuntimeException e) {
            UIUtils.showError(e.getMessage());
        }
    }

    private void moveToList(VBox node) {
        var sourceCardId = ((Card) node.getUserData()).id;

        var targetCardListId = this.cardList.id;

        server.updateCard(sourceCardId, "listDragAndDrop", targetCardListId);

    }

    @FXML
    public void deleteCardList() {
        server.deleteCardList(cardList.id);
    }

    public void stop() {
        server.stop();
    }
}
