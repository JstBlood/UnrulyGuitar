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

    @PutMapping("/{id}/index/s")
    public ResponseEntity<Card> updateIndexS(@PathVariable long id,
                                       @RequestBody int newValue) {
        return ResponseEntity.status(cardService.updateIndex(id, newValue, true)).build();
    }

    @PutMapping("/{id}/index")
    public ResponseEntity<Card> updateIndex(@PathVariable long id,
                                            @RequestBody int newValue) {
        return ResponseEntity.status(cardService.updateIndex(id, newValue, false)).build();
    }

    @PutMapping("/{id}/parent/s")
    public ResponseEntity<Card> updateParentS(@PathVariable long id,
                                            @RequestBody long newValue) {
        return ResponseEntity.status(cardService.updateParent(id, newValue, true)).build();
    }

    @PutMapping("/{id}/parent")
    public ResponseEntity<Card> updateParent(@PathVariable long id,
                                             @RequestBody long newValue) {
        return ResponseEntity.status(cardService.updateParent(id, newValue, false)).build();
    }

    @PutMapping("/{id}/{component}")
    public ResponseEntity<Card> update(@PathVariable long id, @PathVariable String component,
                                       @RequestBody String newValue) {
        return ResponseEntity.status(cardService.update(id, component, newValue)).build();
    }
}
