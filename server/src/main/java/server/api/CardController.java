package server.api;

import commons.Card;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.services.CardService;

@RestController
@RequestMapping(value = {"/secure/{username}/{password}/cards", "/secure/{username}/cards"})
public class CardController {
    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @PostMapping("/add")
    public ResponseEntity<?> add(@RequestBody Card card, @PathVariable String username,
                                      @PathVariable(required = false) String password) {
        return ResponseEntity.status(cardService.add(card, username, password)).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable long id, @PathVariable String username,
                                         @PathVariable(required = false) String password) {
        return ResponseEntity.status(cardService.delete(id, username, password)).build();
    }

    @PutMapping("/{id}/index/s")
    public ResponseEntity<Card> updateIndexS(@PathVariable long id,
                                       @RequestBody int newValue, @PathVariable String username,
                                         @PathVariable(required = false) String password) {
        return ResponseEntity.status(cardService.updateIndex(id, newValue, true, username, password)).build();
    }

    @PutMapping("/{id}/parent/s")
    public ResponseEntity<Card> updateParentS(@PathVariable long id,
                                            @RequestBody long newValue, @PathVariable String username,
                                         @PathVariable(required = false) String password) {
        return ResponseEntity.status(cardService.updateParent(id, newValue, true, username, password)).build();
    }

    @PutMapping("/{id}/{component}")
    public ResponseEntity<?> update(@PathVariable long id, @PathVariable String component,
                                       @RequestBody Object newValue, @PathVariable String username,
                                         @PathVariable(required = false) String password) {
        return ResponseEntity.status(cardService.update(id, component, newValue, username, password)).build();
    }
}
