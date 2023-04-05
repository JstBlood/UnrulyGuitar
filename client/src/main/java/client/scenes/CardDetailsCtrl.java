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
    private VBox subtaskContainer;
    @FXML
    private ChoiceBox<String> presetChoice;

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

        preparePreset();
        prepareTasks();
        prepareTags();
    }

    public void preparePreset() {
        presetChoice.getItems().clear();
        presetChoice.getItems().add("[Default]");

        if(card.colors == null)
            presetChoice.getSelectionModel().select(0);

        for(ColorPreset c : card.parentCardList.parentBoard.cardPresets) {
            presetChoice.getItems().add("No #" + c.id);

            if(card.colors != null && c.id == card.colors.id) {
                presetChoice.getSelectionModel().select(presetChoice.getItems().size()-1);
            }
        }
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
            card.tags.add(t);
            tagsMenu.getItems().remove(newItem);
            addTagsBarNode(t);
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
        FXMLLoader tagLoader = new FXMLLoader(getClass().getResource("/client/scenes/Tag.fxml"));
        tagLoader.setControllerFactory(c ->
                new TagCtrl(this.server, this.mainCtrl, t)
        );
        Node newTagNode = null;
        try {
            newTagNode = tagLoader.load();
            TagCtrl tagCtrl = tagLoader.getController();

            Node finalNewTagNode = newTagNode;

            tagCtrl.delete.setOnAction(e -> {
                tagsBar.getChildren().remove(finalNewTagNode);
                addTagsMenuItem(t);
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

    /**
     * Update the title.
     */
    private void updateTitle() {
        title.setStyle("-fx-text-fill: white;");
    }

    /**
     * Update the description.
     */
    private void updateDescription() {
        description.setStyle("-fx-text-fill: black;");
    }

    private void relink(Card newState) {
        for(Task t : newState.tasks)
            t.parentCard = newState;
    }

    public void submitCardChanges() {
        StringBuilder updates = new StringBuilder("\n");

        updates.append(title.getText()).append("\n");
        updates.append(description.getText()).append("\n");
        for(Tag t : card.tags) {
            updates.append(t.id).append("\n");
        }
        server.updateCard(card.id, "details", updates.toString());
        server.forceRefresh(card.parentCardList.parentBoard.key);

        updatePreset();

        clearFields();
        mainCtrl.showBoardOverview();
    }

    public void submitTagsChanges() {
        StringBuilder updates = new StringBuilder("\n");

        for(Tag t : card.tags) {
            updates.append(t.id).append("\n");
        }
        server.updateCard(card.id, "tags", updates.toString());
        server.forceRefresh(card.parentCardList.parentBoard.key);

        mainCtrl.showBoardOverview();
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
    private Task generateTask() {
        return new Task("New Task", card);
    }

    /**
     * Add a subtask (Task) to our Card.
     */
    public void addTask(){
        server.addTask(generateTask());
    }

    /**
     * Clear all the fields. (used for changing between cards)
     */
    public void clearFields(){
        this.title.clear();
        this.description.clear();
    }
}

