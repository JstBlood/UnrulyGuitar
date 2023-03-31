package client.scenes;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import javax.inject.Inject;

import client.utils.ServerUtils;
import client.utils.UIUtils;
import commons.Card;
import commons.Task;
import jakarta.ws.rs.WebApplicationException;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.*;
import javafx.scene.layout.VBox;

public class CardCtrl implements Initializable {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    private Card card;

    @FXML
    private TextField title;
    @FXML
    private TextArea description;

    @FXML
    private VBox cardBox;

    @FXML
    private ProgressBar prog;

    @Inject
    public CardCtrl(ServerUtils server, MainCtrl mainCtrl, Card c, VBox cardBox) {
        this.server = server;
        this.mainCtrl = mainCtrl;
        this.card = c;
        this.cardBox = cardBox;
    }

    @Override
    public void initialize(URL location, ResourceBundle rs) {
        title.textProperty().addListener((o, oldV, newV) -> {
            if(!Objects.equals(card.title, newV)) {
                title.setStyle("-fx-text-fill: red;");
            }
        });

        this.cardBox.setUserData(card);
        this.cardBox.setOnMouseClicked(e -> {
            if(e.getButton().equals(MouseButton.PRIMARY) && e.getClickCount() == 2) {
                mainCtrl.showCardDetails(card);
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

        title.setText(card.title);

        handleProgress();

        //DRAG AND DROP HANDLERS
        this.cardBox.setOnDragDetected(e -> {
            this.cardBox.setStyle("-fx-opacity: 0.5");

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
        //END OF DRAG AND DROP HANDLER
        this.description.setText(card.description);
        this.description.setPrefRowCount((int) card.description.lines().count());
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

            performDrop(node);

            server.forceRefresh(card.parentCardList.parentBoard.key);

            success = true;
        }

        e.setDropCompleted(success);
        e.consume();
    }

    private void performDrop(VBox node) {
        var sourceID = ((Card) node.getUserData()).id;
        var targetID = card.id;

        if (sourceID != targetID) {
            server.updateCard(sourceID, "dragAndDrop", targetID);
        }

    }

    public void updateTitle() {
        if(title.getText().isEmpty()) {
            title.setText(card.title);
            title.setStyle("-fx-text-fill: white;");
            UIUtils.showError("Title should not be empty!");
            return;
        }

        title.setStyle("-fx-text-fill: white;");

        card.title = title.getText();

        try {
            server.updateCard(card.id, "title", title.getText());
        } catch (WebApplicationException e) {
            UIUtils.showError(e.getMessage());
        }
    }

    @FXML
    public void delete() {
        server.deleteCard(this.card.id);
    }




}
