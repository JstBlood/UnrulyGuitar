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

    @PutMapping("/{id}/title")
    public ResponseEntity<?> updateTitle(@PathVariable long id,
                                         @RequestBody Object newValue, @PathVariable String username,
                                         @PathVariable(required = false) String password) {
        return ResponseEntity.status(cardService.updateTitle(id, newValue, username, password)).build();
    }

    @PutMapping("/{id}/preset")
    public ResponseEntity<?> updatePreset(@PathVariable long id,
                                         @RequestBody long newId, @PathVariable String username,
                                         @PathVariable(required = false) String password) {
        return ResponseEntity.status(cardService.updatePreset(id, newId, username, password)).build();
    }

    @PutMapping("/{id}/description")
    public ResponseEntity<?> updateDescription(@PathVariable long id,
                                               @RequestBody Object newValue, @PathVariable String username,
                                               @PathVariable(required = false) String password) {
        return ResponseEntity.status(cardService.updateDescription(id, newValue, username, password)).build();
    }

    @PutMapping("/{id}/index")
    public ResponseEntity<?> updateIndex(@PathVariable long id,
                                         @RequestBody Object newValue, @PathVariable String username,
                                         @PathVariable(required = false) String password) {
        return ResponseEntity.status(cardService.updateIndex(id, newValue, username, password)).build();
    }

    @PutMapping("/{id}/parentCardList")
    public ResponseEntity<?> updateParent(@PathVariable long id,
                                          @RequestBody Object newValue, @PathVariable String username,
                                          @PathVariable(required = false) String password) {
        return ResponseEntity.status(cardService.updateParent(id, newValue, username, password)).build();
    }

    @PutMapping("/{id}/dragAndDrop")
    public ResponseEntity<?> updateDragAndDrop(@PathVariable long id,
                                               @RequestBody Object newValue, @PathVariable String username,
                                               @PathVariable(required = false) String password) {
        return ResponseEntity.status(cardService.updateDragAndDrop(id, newValue, username, password)).build();
    }

    @PutMapping("/{id}/listDragAndDrop")
    public ResponseEntity<?> updateListDragAndDrop(@PathVariable long id,
                                                   @RequestBody Object newValue, @PathVariable String username,
                                                   @PathVariable(required = false) String password) {
        return ResponseEntity.status(cardService.updateListDragAndDrop(id, newValue, username, password)).build();
    }
}
