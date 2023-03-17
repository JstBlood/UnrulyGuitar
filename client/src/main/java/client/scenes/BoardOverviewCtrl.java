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
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        server.connect();
        server.registerForMessages("/topic/cardlists", CardList.class, q -> {
            Platform.runLater(() -> {
                refresh();
                System.out.println("Refreshed!");
            });
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

    public void refresh() {
        board.cardLists = server.getCardLists(board);

        listsGrid.getChildren().clear();
        listsGrid.getColumnConstraints().clear();

        listsGrid.setAlignment(Pos.TOP_CENTER);

        for (int j = 0; j < board.cardLists.size(); j++) {
            CardList currCardList = board.cardLists.get(j);

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
            this.cardListControllers.add(clctrl);

            listsGrid.add(node, j, 0);

            listsGrid.getColumnConstraints().add(new ColumnConstraints());
        }
        System.out.printf("listsGrid now has %d columns. \n", listsGrid.getColumnCount());
    }

    public void openSettings() {
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
        this.addCardListCtrl.setParentBoard(board);
    }
}
