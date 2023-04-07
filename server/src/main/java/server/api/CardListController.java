/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package server.api;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import commons.CardList;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import server.services.CardListService;

@RestController
@RequestMapping(value = {"/secure/{username}/{password}/lists", "/secure/{username}/lists"})
public class CardListController {
    private final CardListService cardListService;
    private Map<Object, Consumer<CardList>> listeners = new HashMap<>();


    public CardListController(CardListService service) {
        this.cardListService = service;
    }

    @PostMapping("/add")
    public ResponseEntity<?> add(@RequestBody CardList cardList, @PathVariable String username,
                                 @PathVariable(required = false) String password) {
        return ResponseEntity.status(cardListService.add(cardList, username, password)).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable long id, @PathVariable String username,
                                    @PathVariable(required = false) String password) {
        return ResponseEntity.status(cardListService.delete(id, username, password)).build();
    }

    @PutMapping("/{id}/title")
    public ResponseEntity<?> updateTitle(@PathVariable long id,
                                         @RequestBody Object newValue, @PathVariable String username,
                                         @PathVariable(required = false) String password) {
        return ResponseEntity.status(cardListService.updateTitle(id, newValue, username, password)).build();
    }

    @GetMapping("/updates")
    public DeferredResult<ResponseEntity<CardList>> getUpdates() {
        var noContent = ResponseEntity.noContent().build();
        var res = new DeferredResult<ResponseEntity<CardList>>(2000L, noContent);

        var key = new Object();
        listeners.put(key, cardList -> {
            res.setResult(ResponseEntity.ok(cardList));
        });
        res.onCompletion(() -> {
            listeners.remove(key);
        });

        return res;
    }

}
