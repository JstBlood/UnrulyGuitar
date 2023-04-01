package client.scenes;

import java.io.IOException;
import java.net.URL;
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
import javafx.geometry.Pos;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TextField;
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
    private CardDetailsCtrl cardDetailsCtrl;

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

    public void setBoard(Board board) {
        this.board = board;
        inviteKey.getItems().get(0).setText(board.key);
    }

    @Inject
    public BoardOverviewCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
        this.cardDetailsCtrl = new CardDetailsCtrl(server, mainCtrl);

        clipboard = Clipboard.getSystemClipboard();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
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

    public void prepare(Board board) {
        setBoard(board);
        this.board.id = -1;
        try {
            refresh(board);
        } catch (Exception e) {

        }

        server.connect();

        server.registerForMessages("/topic/board/" + board.key + "/deletion", Board.class, q -> {
            Platform.runLater(() -> {
                stop();
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

    public void refresh(Board newState) throws IOException {
        performRelink(newState);

        // If our data is already up-to-date
        // we forgo this update

        // Just as a side note: hashCode does not help with speed here
        // since we already have to go through every field.
        if(board.hashCode() == newState.hashCode()) {
            return;
        }

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

    private void updateBoard(Board newState) {
        setBoard(newState);

        title.setText(board.title);
        title.setStyle("-fx-text-fill: -fx-col-0;");
    }

    private void updateCardLists() throws IOException {
        listsGrid.getChildren().clear();
        listsGrid.getColumnConstraints().clear();
        listsGrid.setAlignment(Pos.TOP_CENTER);

        for (int i = 0; i < board.cardLists.size(); i++) {
            CardList cl = board.cardLists.get(i);
            FXMLLoader cardListLoader = new FXMLLoader(getClass().getResource("/client/scenes/CardList.fxml"));

            cardListLoader.setControllerFactory(c ->
                    new CardListCtrl(this.server, this.mainCtrl, cl)
            );

            VBox cardListNode = cardListLoader.load();

            listsGrid.add(cardListNode, i, 0);
            listsGrid.getColumnConstraints().add(new ColumnConstraints());
        }
    }
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
                        inviteKey.setStyle("-fx-background-color: -fx-col-0");
                    })
            );
            timeline.play();
        });
    }

    @FXML
    public void openSettings() {
        mainCtrl.showBoardSettings();
    }

    @FXML
    public void back() {
        server.deregister();
        mainCtrl.showBoards();
    }

    public void updateTitle() {
        if(title.getText().isEmpty()) {
            title.setText(board.title);
            title.setStyle("-fx-text-fill: -fx-col-0;");
            UIUtils.showError("Title should not be empty!");
            return;
        }

        title.setStyle("-fx-text-fill: -fx-col-0;");

        board.title = title.getText();

        try {
            server.updateBoard(board.key, "title", title.getText());
        } catch (WebApplicationException e) {
            UIUtils.showError(e.getMessage());
        }
    }

    public Board getBoard() {
        return this.board;
    }

    @FXML
    public void addCardList() {
        this.mainCtrl.showAddCardList();
    }

    @FXML
    public void removeBoard() {
        server.deleteBoard(board.key);
    }

    @FXML
    public void leaveBoard() {
        server.leaveBoard(board.key);
        mainCtrl.showBoards();
    }

    public void stop() {
        server.stop();
    }
}
