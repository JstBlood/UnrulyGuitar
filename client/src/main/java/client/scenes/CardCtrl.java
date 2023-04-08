package client.scenes;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javax.inject.Inject;

import client.utils.ServerUtils;
import commons.Card;
import commons.Tag;
import commons.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

public class CardCtrl implements Initializable {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private Card card;

    @FXML
    private ProgressBar prog;
    @FXML
    private Label progress;
    @FXML
    private Label title;
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

        if(card.colors != null)
            mainCtrl.accessUsedPresets().add(card.colors.id);
    }

    private void prepareDragAndDrop() {
        this.cardBox.setUserData(card);

        this.cardBox.setOnMouseClicked(e -> {
            if(e.getButton().equals(MouseButton.PRIMARY) && e.getClickCount() == 2) {
                mainCtrl.showCardDetails(card);
            }
        });

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

    private void handleProgress() {
        int counter = 0;
        for(Task t : card.tasks)
            counter += t.isDone ? 1 : 0;

        prog.setProgress((double)counter/(double)card.tasks.size());
    }

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

    private void setColors() {
        if(card.colors == null) {
            title.setStyle("-fx-text-fill: " + card.parentCardList.parentBoard.defaultPreset.foreground + ";");
        }
        else {
            title.setStyle("-fx-text-fill: " + card.colors.foreground + ";");
            progress.setStyle("-fx-text-fill: " + card.colors.foreground + ";");
        }
    }

    @FXML
    public void delete() {
        server.deleteCard(this.card.id);
    }
}
