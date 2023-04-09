package client.scenes;

import java.io.IOException;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import javax.inject.Inject;

import client.utils.ServerUtils;
import commons.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class CardDetailsCtrl {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private Card card;
    @FXML
    private TextField title;
    @FXML
    private MenuButton tagsMenu;
    @FXML
    private HBox tagsBar;
    @FXML
    private TextArea description;
    @FXML
    private ChoiceBox<String> presetChoice;
    @FXML
    private VBox subtaskContainer;

    /**
     * This controller shows the details of a card.
     * @param server The server connection.
     * @param mainCtrl The main (root) controller.
     */
    @Inject
    public CardDetailsCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }

    /**
     * Populate this controller with an initial Card state.
     * @param card The initial state.
     */
    public void prepare(Card card, Boolean isPopup){
        this.card = card;
        prepareServer();
        if(!isPopup) {
            prepareDetails();
        } else {
            prepareTags();
        }
    }

    private void prepareDetails() {
        refresh(card, true);

        addUpdateHandler(title, () -> updateTitle(), "title", true);
        addUpdateHandler(description, () -> updateDescription(), "description", false);
    }

    public void prepareServer() {
        server.deregister();

        server.connect();

        server.registerForMessages("/topic/board/" + card.parentCardList.parentBoard.key, Board.class, q -> {
            Platform.runLater(() -> {
                try {
                    refresh(q.cardLists.stream().filter(x -> x.id == card.parentCardList.id)
                            .findFirst().get().cards.stream().filter(x -> x.id == card.id).findFirst().get(), false);
                } catch (Exception e) {
                    // skip this because this means that the controller is hidden
                }
            });
        });

        server.registerForMessages("/topic/card/" + card.id + "/deletion", Card.class, q -> {
            Platform.runLater(() -> {
                mainCtrl.showBoardOverview();
            });
        });

        server.registerForMessages("/topic/cardlist/" + card.parentCardList.id + "/deletion", CardList.class, q -> {
            Platform.runLater(() -> {
                mainCtrl.showBoardOverview();
            });
        });

        server.registerForMessages("/topic/board/" + card.parentCardList.parentBoard.key + "/deletion", Board.class,
            q -> {
                Platform.runLater(() -> {
                    mainCtrl.showBoards();
                });
            });
    }


    private void prepareTasks() {
        card.tasks.sort(Comparator.comparingInt(task -> task.index));

        subtaskContainer.getChildren().clear();
        for(Task t : card.tasks) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/client/scenes/Task.fxml"));
                loader.setControllerFactory(c -> new TaskCtrl(server, mainCtrl, t));
                Node taskNode = loader.load();
                subtaskContainer.getChildren().add(taskNode);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void prepareTags() {
        prepareTagsMenu();
        prepareTagsBar();
    }

    private void prepareTagsMenu() {
        List<Tag> remainingTags = mainCtrl.getCurrentBoard().tags;

        for(Tag t : card.tags) {
            Iterator<Tag> it = remainingTags.iterator();
            while(it.hasNext()) {
                Tag curr = it.next();
                if(curr.id == t.id) {
                    it.remove();
                    break;
                }
            }
        }

        tagsMenu.getItems().clear();

        for(Tag t : remainingTags) {
            addTagsMenuItem(t);
        }
    }

    private void addTagsMenuItem(Tag t) {
        MenuItem newItem = new MenuItem(t.name);
        newItem.setUserData(t);

        newItem.setOnAction(e -> {
            server.updateCard(card.id, "addTag", t.id);
        });

        tagsMenu.getItems().add(newItem);
    }

    private void prepareTagsBar() {
        tagsBar.getChildren().clear();

        for(Tag t : card.tags) {
            addTagsBarNode(t);
        }
    }

    private void addTagsBarNode(Tag t) {
        FXMLLoader tagLoader = new FXMLLoader(getClass().getResource("/client/scenes/TagSmall.fxml"));
        tagLoader.setControllerFactory(c ->
                new TagCtrl(this.server, this.mainCtrl, t)
        );
        Node newTagNode = null;
        try {
            newTagNode = tagLoader.load();
            TagCtrl tagCtrl = tagLoader.getController();

            Node finalNewTagNode = newTagNode;

            tagCtrl.name.setEditable(false);

            tagCtrl.delete.setOnAction(e -> {
                server.updateCard(card.id, "removeTag", t.id);
                server.forceRefresh(card.parentCardList.parentBoard.key);
            });

            tagsBar.getChildren().add(newTagNode);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Add a handler for a TextField/TextArea so that it gets updated whenever the user defocuses from it.
     * @param r The control to attach the events to.
     * @param run The function to run on update.
     * @param ff The field of the card to compare the data to.
     * @param onEnter Where or not to attach a event on pressing the enter key.
     */
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
                    r.setStyle("-fx-text-fill: #131313;");
                }
            } );
        }

        r.focusedProperty().addListener((o, oldV, newV) -> {
            if (!newV && r.getStyle().equals("-fx-text-fill: red;")) {
                r.setStyle("-fx-text-fill: #131313;");
            }
        });
    }

    /**
     * Update the title.
     */
    private void updateTitle() {
        title.setStyle("-fx-text-fill: #131313;");
        server.updateCard(card.id, "title", title.getText());
    }

    /**
     * Update the description.
     */
    private void updateDescription() {
        description.setStyle("-fx-text-fill: black;");

        if (description.getText() == null || description.getText().trim().isEmpty()) {
            server.updateCard(card.id, "description", " ");
        } else {
            server.updateCard(card.id, "description", description.getText());
        }
    }

    public void relink(Card newState) {
        for(Task t : newState.tasks)
            t.parentCard = newState;
    }


    /**
     * Refresh the state of this controller.
     * @param newState The new state to merge to our current one.
     * @param pass Whether to perform a comparison between the new one and the old one.
     */
    private void refresh(Card newState, boolean pass) {
        relink(newState);
        card = newState;

        try {
            presetChoice.getItems().clear();
            presetChoice.getItems().add("[Default]");

            if (newState.colors == null)
                presetChoice.getSelectionModel().select(0);

            for (ColorPreset c : newState.parentCardList.parentBoard.cardPresets) {
                presetChoice.getItems().add("No #" + c.id);

                if (newState.colors != null && c.id == newState.colors.id) {
                    presetChoice.getSelectionModel().select(presetChoice.getItems().size() - 1);
                }
            }

            title.setStyle("-fx-text-fill: #131313;");
            description.setStyle("-fx-text-fill: black;");
            title.setText(newState.title);
            description.setText(newState.description);

            prepareTasks();

        } catch (Exception ignored) {

        }
        prepareTags();
    }

    /**
     * Finish editing and return to the board overview.
     */
    public void submitCard() {
        // go back to the overview
        updateDescription();
        updateTitle();
        updatePreset();
        updatePreset();
        back();
    }

    public void cancelCard() {
        back();
    }

    public void back() {
        mainCtrl.showBoardOverview();
    }

    /**
     * Update the chosen preset of our card.
     */
    private void updatePreset() {
        if(presetChoice.getSelectionModel().getSelectedItem().equals("[Default]")) {
            server.updateCardPreset(card.id, -1L);
            return;
        }

        server.updateCardPreset(card.id, Long.parseLong(presetChoice.getSelectionModel().
                getSelectedItem().substring(4)));
    }

    /**
     * Get a new empty task object.
     * @return The task in question.
     */
    public Task generateTask() {
        return new Task("New Task", card);
    }

    /**
     * Add a subtask (Task) to our Card.
     */
    public void addSubtask(){
        server.addTask(generateTask());
    }

    public void setCard(Card card) {
        this.card = card;
    }
}

