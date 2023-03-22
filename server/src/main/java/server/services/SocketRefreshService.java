package server.services;

import commons.Board;
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

}
