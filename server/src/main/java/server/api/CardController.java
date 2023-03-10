package server.api;

import commons.Board;
import commons.Card;
import commons.CardList;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import server.database.BoardRepository;
import server.database.CardListRepository;
import server.database.CardRepository;

@RestController
@RequestMapping("/api/cards")
public class CardController {

    private final CardRepository cardRepository;
    private final CardListRepository cardListRepository;
    private final BoardRepository boardRepository;
    private SimpMessagingTemplate messageTemplate;

    public CardController(CardRepository cardRepository, CardListRepository cardListRepository, BoardRepository boardRepository, SimpMessagingTemplate messageTemplate) {
        this.cardRepository = cardRepository;
        this.cardListRepository = cardListRepository;
        this.boardRepository = boardRepository;
        this.messageTemplate = messageTemplate;
    }

    @PostMapping(path = {"/add"})
    public ResponseEntity<Card> add(@RequestBody Card card, CardList cardList, Board parentBoard) {

        // TODO: implement this

        return ResponseEntity.ok(card);
    }
}
