package server.api;

import commons.Card;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.database.CardRepository;

@RestController
@RequestMapping("/api/cards")
public class CardController {
    private final CardRepository cardRepo;
    private BoardsController boardsController;

    public CardController(CardRepository cardRepo, BoardsController boardsController) {
        this.cardRepo = cardRepo;
        this.boardsController = boardsController;
    }
    @PostMapping("/add")
    public ResponseEntity<Card> add(@RequestBody Card card) {
        if (card == null || isNullOrEmpty(card.title) || card.parentCardList == null) {
            return ResponseEntity.badRequest().build();
        }
        Card saved = cardRepo.save(card);

        boardsController.forceRefresh(card.parentCardList.parentBoard.key);

        return ResponseEntity.ok(saved);
    }

    @PostMapping("/delete")
    public ResponseEntity<Card> delete(@RequestBody Card card) {
        if (card == null || card.parentCardList == null) {
            return ResponseEntity.badRequest().build();
        }
        cardRepo.deleteById(card.id);

        boardsController.forceRefresh(card.parentCardList.parentBoard.key);

        return ResponseEntity.ok(card);
    }

    @PutMapping("/update")
    public ResponseEntity<Card> update(@RequestBody Card card) {
        if (card == null || card.title == null || card.parentCardList == null){
            return ResponseEntity.badRequest().build();
        }
        cardRepo.saveAndFlush(card);

        boardsController.forceRefresh(card.parentCardList.parentBoard.key);

        return ResponseEntity.ok(card);
    }

    private static boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }
}
