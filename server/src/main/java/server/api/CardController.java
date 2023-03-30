package server.api;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import commons.Card;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import server.services.CardService;

@RestController
@RequestMapping(value = {"/secure/{username}/{password}/cards", "/secure/{username}/cards"})
public class CardController {
    private final CardService cardService;
    private Map<Object, Consumer<Card>> listeners = new HashMap<>();

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @PostMapping("/add")
    public ResponseEntity<?> add(@RequestBody Card card, @PathVariable String username,
                                      @PathVariable(required = false) String password) {
        listeners.forEach((k, l) -> {
            l.accept(card);
        });
        return ResponseEntity.status(cardService.add(card, username, password)).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable long id, @PathVariable String username,
                                         @PathVariable(required = false) String password) {
        return ResponseEntity.status(cardService.delete(id, username, password)).build();
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
    public ResponseEntity<?> update(@PathVariable long id, @PathVariable String component,
                                       @RequestBody Object newValue, @PathVariable String username,
                                         @PathVariable(required = false) String password) {
        return ResponseEntity.status(cardService.update(id, component, newValue, username, password)).build();
    }

    @GetMapping("/updates")
    public DeferredResult<ResponseEntity<Card>> getUpdates() {
        var noContent = ResponseEntity.noContent().build();
        var res = new DeferredResult<ResponseEntity<Card>>(2000L, noContent);

        var key = new Object();
        listeners.put(key, card -> {
            res.setResult(ResponseEntity.ok(card));
        });
        res.onCompletion(() -> {
            listeners.remove(key);
        });

        return res;
    }
}
