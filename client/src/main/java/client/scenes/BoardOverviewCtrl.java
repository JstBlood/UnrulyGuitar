package client.scenes;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

import client.utils.ServerUtils;
import client.utils.UIUtils;
import com.google.inject.Inject;
import commons.*;
import jakarta.ws.rs.WebApplicationException;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * This class is the controller of the BoardOverview scene,
 * which is an overview of the board the client is currently on.
 */

public class BoardOverviewCtrl implements Initializable {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private final Clipboard clipboard;
    private Board board;
    private AddCardListCtrl addCardListCtrl;

    @FXML
    private TextField title;
    @FXML
    private MenuButton inviteKey;
    @FXML
    private GridPane listsGrid;
    @FXML
    private HBox section;
    @FXML
    private GridPane rightBar;
    @FXML
    private GridPane rooter;

    @FXML
    private ImageView lock;

    private List<CardListCtrl> children;


    /**
     * This function updates the internal state of the object.
     *
     * @param board The new state to update to.
     */
    private void setBoard(Board board) {
        this.board = board;
        inviteKey.getItems().get(0).setText(board.key);
    }

    /**
     * The main controller that displays all the board with the lists and tasks.
     *
     * @param server The server connection.
     * @param mainCtrl The main (root) controller.
     */
    @Inject
    public BoardOverviewCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;

        children = new ArrayList<>();
        clipboard = Clipboard.getSystemClipboard();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        prepareLongPolling();
        prepareTitleField();
    }

    /**
     * This function "preps" the controller before displaying it.
     * @param board The initial state of the controller.
     */
    public void prepare(Board board) {
        setBoard(board);

        server.connect();

        server.registerForMessages("/topic/board/" + board.key + "/deletion", Board.class, q -> {
            Platform.runLater(() -> {
                mainCtrl.showBoards();
            });
        });

        server.registerForMessages("/topic/board/" + board.key, Board.class, q -> {
            Platform.runLater(() -> {
                try {
                    refresh(q);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        });

        server.forceRefresh(board.key);
    }

    public void prepareLongPolling() {
        server.registerForUpdates(c -> {
            Platform.runLater(() -> {
                server.forceRefresh(board.key);
            });
        });
    }

    /**
     * This function adds all the relevant update listeners to the title field.
     */
    private void prepareTitleField() {
        title.textProperty().addListener((o, oldV, newV) -> {
            if(!Objects.equals(board.title, newV)) {
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

    /**
     * Perform a merging backtracked refresh of the board.
     * @param newState The new state for the controller.
     * @throws IOException Should never be thrown.
     */
    private void refresh(Board newState) throws IOException {
        performRelink(newState);

        updateBoard(newState);

        updateCardLists();
    }

    private void performRelink(Board newState) {
        for(CardList cl : newState.cardLists) {
            cl.parentBoard = newState;

            for(Card c : cl.cards) {
                c.parentCardList = cl;

                for(Task t : c.tasks)
                    t.parentCard = c;

            }
        }

        for(Tag u : newState.tags) {
            u.parentBoard = newState;
        }
    }

    /**
     * Merge the changes of the new state to our current state.
     * @param newState The new state.
     */
    private void updateBoard(Board newState) {
        setBoard(newState);

        if(!newState.title.equals(title.getText())) {
            title.setText(board.title);
        }

        title.setStyle("-fx-text-fill: " + newState.colors.foreground + ";");
        rooter.setStyle("-fx-background-color: " + newState.colors.background + ";");

        mainCtrl.accessUsedPresets().clear();
        mainCtrl.accessUsedPresets().add(newState.defaultPreset.id);

        mainCtrl.updateBoardSettings(newState);

        try {
            if (newState.isPasswordProtected)
                lock.setImage(new Image(getClass()
                        .getResource("/client/images/padlock.png").toURI().toString()));
            else
                lock.setImage(new Image(getClass()
                        .getResource("/client/images/keys.png").toURI().toString()));
        } catch (Exception e) {

        }


        //TODO: update tags
    }

    /**
     * Merge the changes of the new state to our current state, by adding and removing new CardLists.
     * @throws IOException
     */
    private void updateCardLists() throws IOException {
        while(children.size() != board.cardLists.size()) {
            if (children.size() < board.cardLists.size()) {
                CardList cl = board.cardLists.get(children.size());

                FXMLLoader cardListLoader = new FXMLLoader(getClass().getResource("/client/scenes/CardList.fxml"));

                cardListLoader.setControllerFactory(c ->
                        new CardListCtrl(this.server, this.mainCtrl, cl)
                );

                VBox cardListNode = cardListLoader.load();

                listsGrid.add(cardListNode, children.size(), 0);
                listsGrid.getColumnConstraints().add(new ColumnConstraints());
                children.add(cardListLoader.getController());
            } else if (children.size() > board.cardLists.size()) {
                listsGrid.getChildren().remove(children.size() - 1);
                listsGrid.getColumnConstraints().remove(children.size() - 1);
                children.remove(children.size() - 1);
            }
        }

        for(int i = 0; i < children.size(); i++) {
            children.get(i).propagate(board.cardLists.get(i));
        }
    }

    /**
     * Copy the invite key to the clipboard so that you can send it to someone.
     */
    @FXML
    public void getInviteKey() {
        ClipboardContent content = new ClipboardContent();
        content.putString(board.key);
        clipboard.setContent(content);
        Platform.runLater(()->
        {
            Timeline timeline = new Timeline(
                    new KeyFrame(Duration.ZERO, event -> {
                        inviteKey.setText("Copied to clipboard!");
                        inviteKey.setStyle("-fx-background-color: green;");
                    }),
                    new KeyFrame(Duration.seconds(2), event -> {
                        inviteKey.setText("Invite Key");
                        inviteKey.setStyle("-fx-background-color: " + board.colors.foreground + "");
                    })
            );
            timeline.play();
        });
    }

    /**
     * Enter the board settings.
     */
    @FXML
    public void openSettings() {
        mainCtrl.showBoardSettings();
    }

    /**
     * Disconnect from this board.
     */
    @FXML
    public void back() {
        server.deregister();
        mainCtrl.showBoards();
    }

    /**
     * Perform a title update.
     */
    public void updateTitle() {
        if(title.getText().isEmpty()) {
            title.setText(board.title);
            title.setStyle("-fx-text-fill: " + board.colors.foreground + ";");
            UIUtils.showError("Title should not be empty!");
            return;
        }

        title.setStyle("-fx-text-fill: " + board.colors.foreground + ";");

        board.title = title.getText();

        try {
            server.updateBoard(board.key, "title", title.getText());
        } catch (WebApplicationException e) {
            UIUtils.showError(e.getMessage());
        }
    }

    /**
     * Get the current state of this controller.
     * @return current board.
     */
    public Board getBoard() {
        return this.board;
    }

    /**
     * Add a new card list to the board.
     */
    @FXML
    public void addCardList() {
        this.mainCtrl.showAddCardList();
    }

    /**
     * Delete this board from the server.
     */
    @FXML
    public void removeBoard() {
        server.deleteBoard(board.key);
        mainCtrl.showBoards();
    }

    @FXML
    public void performUnlock() {
        mainCtrl.showUnlock();
    }

    /**
     * Leave this board for this user.
     */
    @FXML
    public void leaveBoard() {
        server.leaveBoard(board.key);
        mainCtrl.accessStore().removePassword();
        mainCtrl.showBoards();
    }

    /**
     * Stop all long polling threads.
     */
    @FXML
    public void showHelp() {
        mainCtrl.showHelpScreen("boardOverview");
    }

    public void stop() {
        server.stop();
    }
}
