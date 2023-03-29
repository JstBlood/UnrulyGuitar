package client.scenes;

import client.utils.ServerUtils;
import commons.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

import javax.inject.Inject;
import java.util.List;
import java.util.Objects;

public class CardDetailsCtrl {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private Card card;

    @FXML
    private TextField title;
    @FXML
    private ButtonBar tagsBar;
    @FXML
    private MenuButton addTag;
    @FXML
    private TextArea description;
    @FXML
    private Button submit;
    @FXML
    private ImageView addSubtask;
    @FXML
    private Label addSubtaskLabel;
    @FXML
    private GridPane subtaskPane;
    @FXML
    private TextField subtaskTitle;
    @FXML
    private TextArea subtaskDescription;
    private List<Task> subtasks;

    @Inject
    public CardDetailsCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }

    public void prepare(Card c){
        this.card = c;
        refresh(c, true);

        server.connect();
        server.registerForMessages("/topic/board/" + c.parentCardList.parentBoard.key, Board.class, q -> {
            Platform.runLater(() -> {
                refresh(q.cardLists.stream().filter(x -> x.id == c.parentCardList.id)
                        .findFirst().get().cards.stream().filter(x -> x.id == c.id).findFirst().get(), false);
            });
        });

        EventHandler<ActionEvent> removeTagEvent = e -> {
            this.tagsBar.getButtons().remove((ToggleButton) e.getSource());
        };
        EventHandler<ActionEvent> addTagEvent = e -> {
            String tagName = ((MenuItem) e.getSource()).getText();
            if (this.tagsBar.getButtons().stream().anyMatch(
                    b -> (b instanceof ToggleButton) && ((ToggleButton) b).getText().equals(tagName)))
                return;

            Tag tag = mainCtrl.getCurrentBoard().tags.stream().filter(t ->
                    t.name.equals(tagName)).findFirst().orElse(null);
            ToggleButton tagButton = new ToggleButton(tagName);
            tagButton.setStyle(String.format("-fx-background-color: rgb(%d, %d, %d);",
                    tag.color.getRed(), tag.color.getGreen(), tag.color.getBlue()));
            tagButton.getStyleClass().add("tag");

            tagButton.setOnAction(removeTagEvent);

            tagsBar.getButtons().add(tagButton);
        };

        addUpdateHandler(title, () -> updateTitle(), "title", true);
        addUpdateHandler(description, () -> updateDescription(), "description", false);

        for(Tag tag : mainCtrl.getCurrentBoard().tags){
            MenuItem mi = new MenuItem(tag.name);
            mi.setOnAction(addTagEvent);
            this.addTag.getItems().add(mi);
        }
    }

    private void addUpdateHandler(TextInputControl r, Runnable run, String ff, boolean onEnter) {
        r.textProperty().addListener((o, oldV, newV) -> {
            try {
                if (!Objects.equals(card.getClass().getField(ff), newV)) {
                    r.setStyle("-fx-text-fill: red;");
                }
            } catch (Exception e) {

            }
        });

        if(onEnter) {
            r.setOnKeyPressed(e -> {
                if(e.getCode().equals(KeyCode.ENTER) && r.getStyle().equals("-fx-text-fill: red;")) {
                    run.run();
                }
            } );
        }

        r.focusedProperty().addListener((o, oldV, newV) -> {
            if (!newV && r.getStyle().equals("-fx-text-fill: red;")) {
                run.run();
            }
        });
    }

    private void updateTitle() {
        title.setStyle("-fx-text-fill: white;");
        server.updateCard(card.id, "title", title.getText());
    }

    private void updateDescription() {
        description.setStyle("-fx-text-fill: black;");
        server.updateCard(card.id, "description", description.getText());
    }

    private void relink(Card newState) {
        for(Task t : newState.tasks)
            t.parentCard = newState;
    }

    private void refresh(Card newState, boolean pass) {
        relink(newState);

        if(newState.hashCode() == card.hashCode() && !pass) {
            return;
        }

        title.setStyle("-fx-text-fill: white;");
        description.setStyle("-fx-text-fill: black;");
        title.setText(newState.title);
        description.setText(newState.description);
    }

    public void submitCard(){
        // go back to the overview
        clearFields();
        mainCtrl.showBoardOverview();
    }

    public void addSubtask(){
        System.out.println("Grid: " + this.subtaskPane.toString());
        ImageView iv = new ImageView("file:/client/images/trashcan_icon.png");
        iv.setFitHeight(50);
        iv.setFitWidth(50);
        TextField subtaskTitle = new TextField();
        subtaskTitle.setPrefWidth(300);
        subtaskTitle.setPrefHeight(50);
        subtaskTitle.setStyle("-fx-background-color: yellow;");
        subtaskTitle.setPromptText("Add subtask title...");
        TextArea subtaskDescription = new TextArea();
        subtaskDescription.setPrefWidth(300);
        subtaskDescription.setPrefHeight(100);
        subtaskDescription.setStyle("-fx-background-color: red;");
        subtaskDescription.setPromptText("Add description...");

        this.subtaskPane.add(new Text("SHOW URSELF"), 0, 0);
        this.subtaskPane.add(subtaskTitle, 1, 0);
        this.subtaskPane.add(new Text("PLEASE"), 0, 1);
        this.subtaskPane.add(subtaskDescription, 1, 1);

        System.out.println("Grid is now: "
                + this.subtaskPane.getRowCount()
                + " by "
                + this.subtaskPane.getColumnCount());
        for (Node child : this.subtaskPane.getChildren()) {
            System.out.println(child.toString());
        }
    }

    public void clearFields(){
        this.title.clear();
        this.description.clear();
    }
}

