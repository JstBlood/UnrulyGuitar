package client.scenes;

import java.io.IOException;
import java.util.Objects;
import java.util.Set;
import javax.inject.Inject;

import client.utils.ServerUtils;
import commons.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;

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
    private GridPane subtaskPane;

    @Inject
    public CardDetailsCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }

    public void prepare(Card c) {
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
                q -> {
                    Platform.runLater(() -> {
                        mainCtrl.showBoards();
                    });
                });


        addUpdateHandler(title, () -> updateTitle(), "title", true);
        addUpdateHandler(description, () -> updateDescription(), "description", false);

        prepareTags();
    }

    private void prepareTags() {



    }

    private void prepareTagsMenu() {
        Set<Tag> remainingTags = mainCtrl.getCurrentBoard().tags;
        remainingTags.removeAll(card.tags);

        for(Tag t : remainingTags) {
            MenuItem newItem = new MenuItem(t.name);
            newItem.setUserData(t);

            prepareMenuItem(newItem);

            tagsMenu.getItems().add(newItem);
        }
    }

    private void prepareMenuItem(MenuItem newItem) {
        newItem.setOnAction(event -> {
            Tag newTag = (Tag) newItem.getUserData();
            FXMLLoader tagLoader = new FXMLLoader(getClass().getResource("/client/scenes/Tag.fxml"));
            tagLoader.setControllerFactory(c ->
                    new TagCtrl(this.server, this.mainCtrl, newTag)
            );
            Node newTagNode = null;
            try {
                newTagNode = tagLoader.load();
                //TODO: replace with server call
                //server.updateCard(card.id, "addTag", newTag.id);
                tagsBar.getChildren().add(newTagNode);
                tagsMenu.getItems().remove(newItem);

                TagCtrl tagCtrl = tagLoader.getController();
                Node finalNewTagNode = newTagNode;

                tagCtrl.delete.setOnAction(e -> {
                    //TODO: replace with server call
                    tagsBar.getChildren().remove(finalNewTagNode);
                    tagsMenu.getItems().add(newItem);
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
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

