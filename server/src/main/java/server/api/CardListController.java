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
import commons.Board;
import commons.CardList;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import server.database.CardListRepository;

import java.util.Random;

@RestController
@RequestMapping("/api/cardList")
public class CardListController {
    private final Random random;
    private final CardListRepository listRepo;
    private SimpMessagingTemplate messages;

    public CardListController(Random rng, CardListRepository listRepo, SimpMessagingTemplate messages) {
        this.random = rng;
        this.messages = messages;
        this.listRepo = listRepo;
    }

    @PostMapping(path = {"/add"})
    public ResponseEntity<CardList> add(@RequestBody CardList cardList, Board parentBoard) {
        if (cardList == null || cardList.title == null || parentBoard == null) {
            return ResponseEntity.badRequest().build();
        }
        CardList created = listRepo.save(cardList);
        return ResponseEntity.ok(created);
    }

    @PostMapping(path = {"/delete"})
    public ResponseEntity<CardList> delete(@RequestBody CardList cardList) {
        if (cardList == null || cardList.parentBoard == null) {
            return ResponseEntity.badRequest().build();
        }
        listRepo.deleteById(cardList.id);
        return ResponseEntity.ok(cardList);
    }

    @PostMapping(path = {"/update"})
    public ResponseEntity<CardList> update(@RequestBody CardList cardList) {
        if (cardList == null || cardList.title == null || cardList.parentBoard == null){
            return ResponseEntity.badRequest().build();
        }
        listRepo.saveAndFlush(cardList);
        return ResponseEntity.ok(cardList);
    }

}