package server.services;

import commons.Board;
import commons.Card;
import commons.CardList;
import org.springframework.stereotype.Service;

@Service
public class TestSocketRefresher extends SocketRefreshService {
    public TestSocketRefresher() {
        super(null);
    }

    @Override
    public void broadcast(Board b) {
    }

    @Override
    public void broadcastRemoval(Board b) {
    }

    @Override
    public void broadcastRemoval(Card c) {
    }

    @Override
    public void broadcastRemoval(CardList c) {
    }

}