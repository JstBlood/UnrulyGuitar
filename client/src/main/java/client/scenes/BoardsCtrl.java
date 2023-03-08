package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Board;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.util.Optional;

public class BoardsCtrl {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML
    private TextField bid;

    @Inject
    public BoardsCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    public void prepare() {
        server.connect();
        server.registerForMessages("/topic/board", Optional.class, (x) -> handleIncoming(x));
    }

    public void join() {
        server.send("/app/board/join", Long.parseLong(bid.getText()));
    }

    public void create() {
        server.send("/app/board/create", 0);
    }

    private void handleIncoming(Optional<Board> msg) {
        System.out.println("board: " + msg);
    }

}
