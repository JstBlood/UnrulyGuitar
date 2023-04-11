package client.scenes;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import javax.inject.Inject;

import client.utils.ServerUtils;
import client.utils.UIUtils;
import commons.Card;
import commons.Tag;
import commons.Task;
import jakarta.ws.rs.WebApplicationException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.input.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

public class CardCtrl implements Initializable {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private Card card;

    @FXML
    private ProgressBar progressBar;
    @FXML
    private Label progressLabel;
    @FXML
    private TextField title;
    @FXML
    private HBox tagContainer;
    @FXML
    private HBox imageContainer;
    @FXML
    private HBox editContainer;
    @FXML
    private HBox deleteContainer;
    @FXML
    private VBox cardBox;

    @Inject
    public CardCtrl(ServerUtils server, MainCtrl mainCtrl, Card c, VBox cardBox) {
        this.server = server;
        this.mainCtrl = mainCtrl;
        this.card = c;
        this.cardBox = cardBox;
    }

    @Override
    public void initialize(URL location, ResourceBundle rs) {
        handleProgress();

        prepareDragAndDrop();

        prepareTitleField();

        prepareOther();

        if(card.colors != null)
            mainCtrl.accessUsedPresets().add(card.colors.id);
    }

    /**
     * checks whether a user wants to open card details or drag and drop a card, then executes the option
     */
    private void prepareDragAndDrop() {
        this.cardBox.setUserData(card);

        this.cardBox.setOnMouseClicked(e -> {
            if(e.getButton().equals(MouseButton.PRIMARY) && e.getClickCount() == 2) {
                mainCtrl.showCardDetails(card);
            }
        });

        this.cardBox.setOnDragDetected(e -> {
            this.cardBox.setStyle("-fx-opacity: 0.2");

            Dragboard db = cardBox.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString(String.valueOf(this.card.id));
            db.setContent(content);
            e.consume();
        });

        this.cardBox.setOnDragDone(e -> {
            this.cardBox.setStyle("-fx-opacity: 1");
            e.consume();
        });

        this.cardBox.setOnDragDropped(e -> {
            handleDrop(e);
        });
    }

    private void prepareTitleField() {
        title.setOnMouseClicked(e -> {
            if(e.getButton().equals(MouseButton.PRIMARY) && e.getClickCount() == 2){
                mainCtrl.showCardDetails(card);
            }
        });
        title.textProperty().addListener((o, oldV, newV) -> {
            if(!Objects.equals(card.title, newV)) {
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

        title.setEditable(false);

        title.setText(card.title);
    }

    private void prepareOther() {
        this.editContainer.setOnMouseClicked(e -> {
            if (e.getButton().equals(MouseButton.PRIMARY)) {
                mainCtrl.showCardDetails(card);
            }
        });

        this.deleteContainer.setOnMouseClicked(e -> {
            if (e.getButton().equals(MouseButton.PRIMARY)) {
                delete();
            }
        });

        this.progressLabel.setOnMouseClicked(e -> {
            if(e.getButton().equals(MouseButton.PRIMARY) && e.getClickCount() == 2){
                mainCtrl.showCardDetails(card);
            }
        });

        this.progressBar.setOnMouseClicked(e -> {
            if(e.getButton().equals(MouseButton.PRIMARY) && e.getClickCount() == 2){
                mainCtrl.showCardDetails(card);
            }
        });
    }
    private void handleProgress() {
        int counter = 0;
        for(Task t : card.tasks)
            counter += t.isDone ? 1 : 0;

        progressBar.setProgress((double)counter/(double)card.tasks.size());
    }

    /**
     * 'drops'the card
     * @param e
     */
    private void handleDrop(DragEvent e) {
        Dragboard db = e.getDragboard();
        boolean success = false;

        if (db.hasString()) {
            var node = (VBox) e.getGestureSource();
            var sourceID = ((Card) node.getUserData()).id;

            if (sourceID != card.id) {
                server.updateCard(sourceID, "dragAndDrop", card.id);
                server.forceRefresh(card.parentCardList.parentBoard.key);
            }

            success = true;
        }
        e.setDropCompleted(success);
        e.consume();
    }

    public void propagate(Card newState) {
        if(!newState.title.equals(title.getText())) {
            title.setText(newState.title);
        }

        if(newState.colors != null)
            mainCtrl.accessUsedPresets().add(newState.colors.id);

        setColors();

        if (newState.description.trim().isEmpty()) {
            imageContainer.setStyle("visibility: hidden");
        }

        if(card.colors == null) {
            cardBox.setStyle("-fx-background-color: " + card.parentCardList.parentBoard
                    .defaultPreset.background + " ");
            tagContainer.setStyle("-fx-background-color: " + card.parentCardList.parentBoard
                    .defaultPreset.background + " ");
        } else {
            cardBox.setStyle("-fx-background-color: " + card.colors.background + " ");
            tagContainer.setStyle("-fx-background-color: " + card.colors.background + " ");
        }

        card = newState;

        handleProgress();
        showTags();
    }

    /**
     * shows the tags of a card
     */
    public void showTags() {
        tagContainer.getChildren().clear();

        int count = 0;

        for(Tag tag : card.tags) {

            if(count < 3) {

                Circle tagCircle = new Circle(7, Paint.valueOf(tag.colors.background));

                tagContainer.getChildren().addAll(tagCircle);

                count++;
            }
        }
    }

    /**
     * sets the colour of cards to default of the board
     */
    private void setColors() {
        if(card.colors == null) {
            title.setStyle("-fx-text-fill: " + card.parentCardList.parentBoard.defaultPreset.foreground + ";");
            progressLabel.setStyle("-fx-text-fill: " + card.parentCardList.parentBoard.defaultPreset.foreground + ";");
            cardBox.setStyle("-fx-border-color: " + card.parentCardList.parentBoard.defaultPreset.foreground + ";");
        }
        else {
            title.setStyle("-fx-text-fill: " + card.colors.foreground + ";");
            progressLabel.setStyle("-fx-text-fill: " + card.colors.foreground + ";");
            cardBox.setStyle("-fx-border-color: " + card.colors.foreground + ";");
        }
    }

    /**
     * updates the title of a card
     */
    public void updateTitle() {
        title.setEditable(false);
        title.setFocusTraversable(false);

        if (title.getText().isEmpty()) {
            title.setText(card.title);
            UIUtils.showError("Title should not be empty!");
            return;
        }

        card.title = title.getText();

        try {
            server.updateCard(card.id, "title", title.getText());
        } catch (WebApplicationException e) {
            UIUtils.showError(e.getMessage());
        }
    }

    public void setEditableTitle() {
        Platform.runLater(() -> {
            title.setEditable(true);
            title.setFocusTraversable(true);
            title.requestFocus();
        });
    }

    @FXML
    public void delete() {
        server.deleteCard(this.card.id);
    }
}
