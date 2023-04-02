package client.scenes;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import javax.inject.Inject;

import client.utils.ServerUtils;
import commons.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

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
                try {
                    refresh(q.cardLists.stream().filter(x -> x.id == c.parentCardList.id)
                            .findFirst().get().cards.stream().filter(x -> x.id == c.id).findFirst().get(), false);
                } catch (Exception e) {
                    // skip this because this means that the controller is hidden
                }
            });
        });

        server.registerForMessages("/topic/card/" + c.id + "/deletion", Card.class, q -> {
            Platform.runLater(() -> {
                mainCtrl.showBoardOverview();
            });
        });

        server.registerForMessages("/topic/cardlist/" + c.parentCardList.id + "/deletion", CardList.class, q -> {
            Platform.runLater(() -> {
                mainCtrl.showBoardOverview();
            });
        });

        server.registerForMessages("/topic/board/" + c.parentCardList.parentBoard.key + "/deletion", Board.class,
                q -> { Platform.runLater(() -> { mainCtrl.showBoards(); }); });

        EventHandler<ActionEvent> addTagEvent = registerTagEvents();

        addUpdateHandler(title, () -> updateTitle(), "title", true);
        addUpdateHandler(description, () -> updateDescription(), "description", false);

        for(Tag tag : mainCtrl.getCurrentBoard().tags){
            MenuItem mi = new MenuItem(tag.name);
            mi.setOnAction(addTagEvent);
            this.addTag.getItems().add(mi);
        }
    }

    private EventHandler<ActionEvent> registerTagEvents() {
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
        return addTagEvent;
    }

    private void addUpdateHandler(TextInputControl r, Runnable run, String ff, boolean onEnter) {
        r.textProperty().addListener((o, oldV, newV) -> {
            try {
                if (!Objects.equals(card.getClass().getField(ff).get(card), newV)) {
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

        card = newState;

        title.setStyle("-fx-text-fill: white;");
        description.setStyle("-fx-text-fill: black;");
        title.setText(newState.title);
        description.setText(newState.description);

        subtaskPane.getRowConstraints().clear();
        subtaskPane.getChildren().clear();

        for(Task t : card.tasks) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/client/scenes/Task.fxml"));
                loader.setControllerFactory(c -> new TaskCtrl(server, mainCtrl, t));
                Parent root = loader.load();
                subtaskPane.add(root, 0, t.index);
                subtaskPane.getRowConstraints().add(new RowConstraints());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void submitCard(){
        // go back to the overview
        clearFields();
        mainCtrl.showBoardOverview();
    }

    private Task generateTask() {
        return new Task("New Task", card);
    }

    public void addSubtask(){
        server.addTask(generateTask());
    }

    public void clearFields(){
        this.title.clear();
        this.description.clear();
    }
}

