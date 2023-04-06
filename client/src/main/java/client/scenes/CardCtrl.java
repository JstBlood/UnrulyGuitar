package client.scenes;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import javax.inject.Inject;
import javax.swing.text.Element;
import javax.swing.text.html.ImageView;

import client.utils.ServerUtils;
import client.utils.UIUtils;
import commons.Card;
import commons.Tag;
import commons.Task;
import jakarta.ws.rs.WebApplicationException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class CardCtrl implements Initializable {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private Card card;

    @FXML
    private TextField title;
    @FXML
    private VBox cardBox;
    @FXML
    private HBox tagContainer;
    @FXML
    private ProgressBar prog;
    @FXML
    private ImageView descIcon;
    @FXML
    private ImageView editIcon;

    @Inject
    public CardCtrl(ServerUtils server, MainCtrl mainCtrl, Card c, VBox cardBox) {
        this.server = server;
        this.mainCtrl = mainCtrl;
        this.card = c;
        this.cardBox = cardBox;
    }

    @Override
    public void initialize(URL location, ResourceBundle rs) {
        prepareCard();

        handleProgress();

        prepareDragAndDrop();

//        this.description.setText(card.description);
//        this.description.setPrefRowCount((int) card.description.lines().count());
        if(card.colors != null)
            mainCtrl.accessUsedPresets().add(card.colors.id);
    }

    private void prepareCard() {
//        title.textProperty().addListener((o, oldV, newV) -> {
//            if(!Objects.equals(card.title, newV)) {
//                title.setStyle("-fx-text-fill: red;");
//            }
//        });
//        title.setOnKeyPressed(e -> {
//            if(e.getCode().equals(KeyCode.ENTER) && title.getStyle().equals("-fx-text-fill: red;")) {
//                updateTitle();
//            }
//        } );
//        title.focusedProperty().addListener((o, oldV, newV) -> {
//            if(!newV && title.getStyle().equals("-fx-text-fill: red;")) {
//                updateTitle();
//            }
//        });
//        title.setEditable(false);
//
//        if (!card.description.isEmpty()) {
//            descIcon.append(new ImageView((Element) new Image("@/client/images/desc_icon.png"))); ;
//        }

        title.setText(card.title);
    }

    private void prepareDragAndDrop() {
        this.cardBox.setUserData(card);

        this.cardBox.setOnMouseClicked(e -> {
            if(e.getButton().equals(MouseButton.PRIMARY) && e.getClickCount() == 2) {
                mainCtrl.showCardDetails(card);
            }
        });

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

        setTitleColors();

//        if(!newState.description.equals(description.getText())) {
//            description.setText(newState.description);
//        }

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

        for(Tag tag : card.tags) {
            FXMLLoader tagLoader = new FXMLLoader(getClass().getResource("/client/scenes/TagSmall.fxml"));

            tagLoader.setControllerFactory(c ->
                    new TagSmallCtrl(tag)
            );

            Node newTagNode = null;
            try {
                newTagNode = tagLoader.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            tagContainer.getChildren().add(newTagNode);
        }
    }

    private void setTitleColors() {
        if(card.colors == null)
            title.setStyle("-fx-text-fill: " + card.parentCardList.parentBoard.defaultPreset.foreground + ";");
        else
            title.setStyle("-fx-text-fill: " + card.colors.foreground + ";");
    }

    public void updateTitle() {
        title.setEditable(false);
        title.setFocusTraversable(false);

        if(title.getText().isEmpty()) {
            title.setText(card.title);
            setTitleColors();
            UIUtils.showError("Title should not be empty!");
            return;
        }

        setTitleColors();

        card.title = title.getText();

        try {
            server.updateCard(card.id, "title", title.getText());
        } catch (WebApplicationException e) {
            UIUtils.showError(e.getMessage());
        }
    }

    public void setEditableTitle() {
        title.setEditable(true);
        title.setFocusTraversable(true);
        title.requestFocus();
    }

    @FXML
    public void delete() {
        server.deleteCard(this.card.id);
    }
}
