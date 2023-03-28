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
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

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
    private AddCardCtrl addCardCtrl;

    @FXML
    private TextField title;
    @FXML
    private MenuItem inviteKey;
    @FXML
    private GridPane listsGrid;
    @FXML
    private HBox section;

    public void setBoard(Board board) {
        this.board = board;
        inviteKey.setText(board.key);
    }

    public Board getBoard() {
        return this.board;
    }

    @Inject
    public BoardOverviewCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
        //TODO: I do not think the next line should be in here :))
        addCardCtrl = new AddCardCtrl(server, mainCtrl);

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


        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/client/scenes/AddCardList.fxml"));
            loader.setControllerFactory(c -> new AddCardListCtrl(server, mainCtrl));
            Parent root = loader.load();
            this.addCardListCtrl = loader.getController();
            section.getChildren().add(root);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void prepare(Board board) {
        setBoard(board);

        server.connect();
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
            u.board = newState;
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

        for (CardList cl : board.cardLists) {
            FXMLLoader cardListLoader = new FXMLLoader(getClass().getResource("/client/scenes/CardList.fxml"));

            cardListLoader.setControllerFactory(c ->
                    new CardListCtrl(this.server, this.mainCtrl, cl)
            );

            VBox cardListNode = cardListLoader.load();

            listsGrid.add(cardListNode, cl.index, 0);
            listsGrid.getColumnConstraints().add(new ColumnConstraints());
        }
    }
    @FXML
    public void getInviteKey() {
        ClipboardContent content = new ClipboardContent();
        content.putString(board.key);
        clipboard.setContent(content);
        UIUtils.showMessage("Copied to clipboard!");
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
            title.setStyle("-fx-text-fill: white;");
            UIUtils.showError("Title should not be empty!");
            return;
        }

        title.setStyle("-fx-text-fill: white;");

        board.title = title.getText();

        try {
            server.updateBoard(board.key, "title", title.getText());
        } catch (WebApplicationException e) {
            UIUtils.showError(e.getMessage());
        }
    }
}
