package client.scenes;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Board;
import commons.Card;
import commons.CardList;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.TextField;
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
    private Board board;
    private AddCardListCtrl addCardListCtrl;
    private List<CardListCtrl> cardListControllers;
    private AddCardCtrl addCardCtrl;
    @FXML
    private TextField boardTitle;
    @FXML
    private GridPane listsGrid;
    @FXML
    private HBox section;

    @Inject
    public BoardOverviewCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
        this.cardListControllers = new ArrayList<>();
        this.addCardCtrl = new AddCardCtrl(server, mainCtrl);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        boardTitle.setOnKeyPressed(e -> {
            if(e.getCode().equals(KeyCode.ENTER)) {
                editTitle();
            }
        } );
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
        this.board = board;
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
        updateBoard(newState);
        updateCardLists();

        System.out.println("[REFRESH]");
    }

    private void updateBoard(Board board) {
        this.board = board;
        boardTitle.setText(board.title);
    }

    private void updateCardLists() throws IOException {
        listsGrid.getChildren().clear();
        listsGrid.getColumnConstraints().clear();
        listsGrid.setAlignment(Pos.TOP_CENTER);

        loadCardLists();
    }

    public void loadCardLists() throws IOException {
        for (int j = 0; j < board.cardLists.size(); j++) {
            CardList currCardList = board.cardLists.get(j);

            //TODO: remove this line
            currCardList.parentBoard = this.board;

            FXMLLoader cardListLoader = new FXMLLoader(getClass().getResource("/client/scenes/CardList.fxml"));
            cardListLoader.setControllerFactory(c -> new CardListCtrl(this.server, this.mainCtrl));

            VBox cardListNode = cardListLoader.load();
            CardListCtrl ctrl = cardListLoader.getController();

            ctrl.setup(currCardList);

            cardListControllers.add(ctrl);

            loadCards(ctrl);

            listsGrid.add(cardListNode, j, 0);

            listsGrid.getColumnConstraints().add(new ColumnConstraints());
        }
    }

    public void loadCards(CardListCtrl clctrl) throws IOException {
        for (Card currCard : clctrl.cardList.cards) {
            FXMLLoader cardLoader = new FXMLLoader(getClass().getResource("/client/scenes/Card.fxml"));
            cardLoader.setControllerFactory(c -> new CardCtrl(this.server, this.mainCtrl));

            VBox cardNode = cardLoader.load();
            CardCtrl cctrl = cardLoader.getController();

            cctrl.setup(currCard);

            clctrl.addCardToContainer(cardNode);
        }
    }

    @FXML
    public void editTitle() {
        server.editBoardTitle(board.key, boardTitle.getText());
        System.out.println("[DEBUG]: Title changed to " + boardTitle.getText());
    }

    public void openSettings() {
        mainCtrl.showBoardSettings();
    }

    public void setBoard(Board board) {
        this.board = board;
        this.addCardListCtrl.setParentBoard(board);
        this.addCardCtrl.setParentBoard(board);
    }
    public void back(){
        mainCtrl.showBoards();
    }
}
