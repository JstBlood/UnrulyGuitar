package client.scenes;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Board;
import commons.CardList;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

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
    private Label boardTitle;
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
            Platform.runLater(() -> refresh(q));
        });
        server.forceRefresh(board.key);
    }

    public void refresh(Board newState) {
        board = newState;

        listsGrid.getChildren().clear();
        listsGrid.getColumnConstraints().clear();

        listsGrid.setAlignment(Pos.TOP_CENTER);

        for (int j = 0; j < board.cardLists.size(); j++) {
            CardList currCardList = board.cardLists.get(j);

            currCardList.parentBoard = this.board;
            System.out.printf("[REFRESH]: Received CardList %s", currCardList);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/client/scenes/CardList.fxml"));
            loader.setControllerFactory(c -> new CardListCtrl(this.server, this.mainCtrl, new ListView<>()));

            Node node;
            try {
                node = loader.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            CardListCtrl clctrl = loader.getController();
            clctrl.title.setText(currCardList.title);
            clctrl.setCardList(currCardList);
            this.cardListControllers.add(clctrl);

            listsGrid.add(node, j, 0);

            listsGrid.getColumnConstraints().add(new ColumnConstraints());
        }
        System.out.printf("listsGrid now has %d columns. \n", listsGrid.getColumnCount());
    }

    public void openSettings() {
        mainCtrl.showBoardSettings();
    }

    public Board getBoard() {
        return board;
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
