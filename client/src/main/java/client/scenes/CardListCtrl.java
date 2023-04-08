package client.scenes;

import java.net.URL;
import java.util.*;

import client.utils.ServerUtils;
import client.utils.UIUtils;
import com.google.inject.Inject;
import commons.Card;
import commons.CardList;
import jakarta.ws.rs.WebApplicationException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.effect.Glow;
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
    private List<CardCtrl> children;

    @Inject
    public CardListCtrl(ServerUtils server, MainCtrl mainCtrl, CardList cardList) {
        this.server = server;
        this.mainCtrl = mainCtrl;
        this.cardList = cardList;
        children = new ArrayList<>();
    }


    @FXML
    @Override
    public void initialize(URL uri, ResourceBundle rs) {
        prepareTitleField();
        prepareDragAndDrop();
        showCards();
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

    private void prepareDragAndDrop() {
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
    }

    public void showCards() {
        cardList.cards.sort(Comparator.comparingInt(card -> card.index));

        cardsContainer.getChildren().clear();

        for(Card c : cardList.cards) {
            FXMLLoader cardLoader = new FXMLLoader(getClass().getResource("/client/scenes/Card.fxml"));
            cardLoader.setControllerFactory(g -> new CardCtrl(this.server, this.mainCtrl, c, cardsContainer));
            try {
                VBox cardNode = cardLoader.load();
                CardCtrl cardCtrl = cardLoader.getController();

                cardNode.setUserData(c);
                prepareCardNode(cardNode, cardCtrl);

                cardsContainer.getChildren().add(cardNode);

                cardCtrl.propagate(c);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void prepareCardNode(Node cardNode, CardCtrl cardCtrl) {
        prepareCardFocus(cardNode);
        prepareCardTitle(cardNode);
        prepareCardKeyEvents(cardNode, cardCtrl);
    }

    public void prepareCardFocus(Node cardNode) {
        cardNode.setFocusTraversable(true);

        cardNode.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue) {
                cardNode.setEffect(new Glow(1));
            } else {
                cardNode.setEffect(null);
            }
        });

        cardNode.setOnMouseEntered(e -> {
            cardNode.setEffect(new Glow(1));
            e.consume();
        });
        cardNode.setOnMouseExited(e -> {
            cardNode.setEffect(null);
            e.consume();
        });
    }

    @SuppressWarnings("checkstyle:CyclomaticComplexity")
    public void prepareCardKeyEvents(Node cardNode, CardCtrl cardCtrl) {
        cardNode.setOnKeyPressed(e -> {
            if (cardNode.isFocused()) {
                Card card = (Card) cardNode.getUserData();
                if (e.isShiftDown() && e.getCode().equals(KeyCode.UP) && card.index > 0) {
                    Card prev = cardList.cards.get(card.index - 1);
                    server.updateCard(card.id, "swap", prev.id);
                } else if (e.isShiftDown() && e.getCode().equals(KeyCode.DOWN) &&
                        card.index < cardList.cards.size() - 1) {
                    Card next = cardList.cards.get(card.index + 1);
                    server.updateCard(card.id, "swap", next.id);
                } else if(e.getCode().equals(KeyCode.E)) {
                    //TODO : edit title

                } else if (e.getCode().equals(KeyCode.BACK_SPACE) || e.getCode().equals(KeyCode.DELETE)) {
                    server.deleteCard(card.id);
                } else if(e.getCode().equals(KeyCode.ENTER)) {
                    mainCtrl.showCardDetails(card);
                } else {
                    switch(e.getCode()) {
                        case E:
                            //TODO: New Scene where edit only title.
//                            cardCtrl.setEditableTitle();
                            break;
                        case BACK_SPACE:
                            server.deleteCard(card.id);
                            break;
                        case DELETE:
                            server.deleteCard(card.id);
                            break;
                        case ENTER:
                            mainCtrl.showCardDetails(card);
                            break;
                        case T:
                            mainCtrl.showTagsPopup(card);
                            break;
                        case C:
                            //TODO: create popup for color selection
                            break;
                    }
                }
            }
        });
    }

    public void prepareCardTitle(Node cardNode) {
        Card card = (Card) cardNode.getUserData();
    }

    public void propagate(CardList newState) {
        title.setStyle("-fx-text-fill: " + newState.parentBoard.cardListColors.foreground + ";");
        mainContainer.setStyle("-fx-background-color: " + newState.parentBoard.cardListColors.background + ";");
        if(!newState.title.equals(title.getText())) {
            title.setText(newState.title);
        }

        cardList = newState;

        showCards();
    }

    @FXML
    public void cardAdd() {
        if(cardName.getText().trim().equals("")) {
            UIUtils.showError("Card name cannot be empty");
            return;
        }

        try {
            Card newCard = generateCard();
            server.addCard(newCard);
        } catch (WebApplicationException e) {
            UIUtils.showError(e.getMessage());
        }

        cardName.setText("");
    }

    private void handleDragEvent(DragEvent e) {
        Dragboard db = e.getDragboard();
        boolean success = false;

        if (db.hasString()) {
            var node = (VBox) e.getGestureSource();
            var sourceCardId = ((Card) node.getUserData()).id;

            server.updateCard(sourceCardId, "listDragAndDrop", cardList.id);
            server.forceRefresh(cardList.parentBoard.key);

            success = true;
        }
        e.setDropCompleted(success);
        e.consume();
    }

    public void updateTitle() {
        if (title.getText().isEmpty()) {
            title.setText(cardList.title);
            title.setStyle("-fx-text-fill: " + cardList.parentBoard.cardListColors.foreground + ";");
            UIUtils.showError("Title should not be empty!");
            return;
        }

        cardList.title = title.getText();
        title.setStyle("-fx-text-fill: " + cardList.parentBoard.cardListColors.foreground + ";");

        try {
            server.updateCardList(cardList.id, "title", title.getText());
        } catch (RuntimeException e) {
            UIUtils.showError(e.getMessage());
        }
    }

    private Card generateCard() {
        return new Card(cardName.getText(), "", this.cardList);
    }

    @FXML
    public void deleteCardList() {
        server.deleteCardList(cardList.id);
    }
}
