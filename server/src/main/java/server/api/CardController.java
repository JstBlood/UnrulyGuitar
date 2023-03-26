package server.api;

import commons.Card;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.services.CardService;

@RestController
@RequestMapping("/api/cards")
public class CardController {
    private CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }
    @PostMapping("/add")
    public ResponseEntity<Card> add(@RequestBody Card card) {
        return ResponseEntity.status(cardService.add(card)).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Card> delete(@PathVariable long id) {
        return ResponseEntity.status(cardService.delete(id)).build();
    }

    @PutMapping("/{id}/{component}")
    public ResponseEntity<Card> update(@PathVariable long id, @PathVariable String component,
                                       @RequestBody String newValue) {
        return ResponseEntity.status(cardService.update(id, component, newValue)).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Card> get(@PathVariable long id) {
        return cardService.get(id);
    }

    private static boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }
}
