package server.services;

import commons.Board;
import commons.Card;
import commons.CardList;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class SocketRefreshService {
    private SimpMessagingTemplate messages;

    public SocketRefreshService(SimpMessagingTemplate simp) {
        messages = simp;
    }

    public void broadcast(Board b) {
        messages.convertAndSend("/topic/board/" + b.key, b);
    }

    public void broadcastRelist() {
        messages.convertAndSend("/topic/relist/", new Board());
    }

    public void broadcastRemoval(Board b) {
        messages.convertAndSend("/topic/board/" + b.key + "/deletion", b);
    }

    public void broadcastRemoval(Card c) {
        messages.convertAndSend("/topic/card/" + c.id + "/deletion", c);
    }

    public void broadcastRemoval(CardList c) {
        messages.convertAndSend("/topic/cardlist/" + c.id + "/deletion", c);
    }

}
