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

import commons.CardList;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.services.CardListService;

@RestController
@RequestMapping(value = {"/secure/{username}/{password}/lists", "/secure/{username}/lists"})
public class CardListController {
    private final CardListService cardListService;

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

    @PutMapping("/{id}/{component}")
    public ResponseEntity<?> update(@PathVariable long id, @PathVariable String component,
                                    @RequestBody Object newValue, @PathVariable String username,
                                    @PathVariable(required = false) String password) {
        return ResponseEntity.status(cardListService.update(id, component, newValue, username, password)).build();
    }

}
