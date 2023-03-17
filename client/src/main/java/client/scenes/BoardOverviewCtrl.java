package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Board;
import commons.CardList;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;

/**
 * This class is the controller of the BoardOverview scene,
 * which is an overview of the board the client is currently on.
 */

public class BoardOverviewCtrl {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private Board board;
    @FXML
    private Label boardTitle;
    @FXML
    private GridPane listsGrid;

    @Inject
    public BoardOverviewCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    public void prepare(Board board) {
        this.board = board;

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

        for(int j = 0; j < board.cardLists.size(); j++) {
            CardList currCardList = board.cardLists.get(j);

            //TODO: replace the Label node with the List node
            Node title = new Label(currCardList.title);
            listsGrid.add(title, j, 0);

            listsGrid.getColumnConstraints().add(new ColumnConstraints(120));
        }

        Button addNewList = new Button("Add new list");
        addNewList.setOnMouseClicked(e -> addCardList());

        listsGrid.add(addNewList, board.cardLists.size(), 0);
    }

    public void addCardList() {
        mainCtrl.showAddCardList();
    }

    public void editCardListTitle() {
        //TODO: initialize, load and implement the feature of editing a card list title by just clicking on it
    }

    public void editBoard() {}

    public Board getBoard() {
        return board;
    }
}
