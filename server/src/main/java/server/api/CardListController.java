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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.services.CardListService;

@RestController
@RequestMapping("/api/cardlists")
public class CardListController {
    private final CardListService cardListService;

    public CardListController(CardListService service) {
        this.cardListService = service;
    }

    @PostMapping("/add")
    public ResponseEntity<?> add(@RequestBody CardList cardList) {
        if(!cardListService.add(cardList)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable long id) {
        if(!cardListService.delete(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/{component}")
    public <T> ResponseEntity<?> update(@PathVariable long id,
                                        @PathVariable String component,
                                        @RequestBody String newValue) {
        if(!cardListService.update(id, component, newValue)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok().build();
    }

}
