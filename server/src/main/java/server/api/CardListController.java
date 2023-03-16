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

import java.util.List;
import java.util.Random;

import commons.Board;
import commons.CardList;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import server.database.CardListRepository;

@RestController
@RequestMapping("/api/cardlists")
public class CardListController {
    private final Random random;
    private final CardListRepository listRepo;
    private SimpMessagingTemplate messages;

    public CardListController(Random rng, CardListRepository listRepo, SimpMessagingTemplate messages) {
        this.random = rng;
        this.messages = messages;
        this.listRepo = listRepo;
    }

    @PostMapping("/get/all")
    public ResponseEntity<List<CardList>> getAll(@RequestBody Board board) {
        if (board == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(listRepo.findByParentBoard(board));
    }

    @MessageMapping("/cardlists/add")
    @SendTo("/topic/cardlists")
    public CardList addMessage(CardList cardList) {
        add(cardList);
        return cardList;
    }

    @MessageMapping("/cardlists/edit/title")
    @SendTo("/topic/cardlists")
    public CardList editTitleMessage(CardList cardList) {
        update(cardList);
        return cardList;
    }

    @PostMapping(path = {"/add"})
    public ResponseEntity<CardList> add(@RequestBody CardList cardList) {
        if (cardList == null || isNullOrEmpty(cardList.title) || cardList.parentBoard == null) {
            return ResponseEntity.badRequest().build();
        }
        CardList saved = listRepo.save(cardList);
        return ResponseEntity.ok(saved);
    }

    @PostMapping(path = {"/delete"})
    public ResponseEntity<CardList> delete(@RequestBody CardList cardList) {
        if (cardList == null || cardList.parentBoard == null) {
            return ResponseEntity.badRequest().build();
        }
        listRepo.deleteById(cardList.id);
        return ResponseEntity.ok(cardList);
    }

    @PutMapping(path = {"/update"})
    public ResponseEntity<CardList> update(@RequestBody CardList cardList) {
        if (cardList == null || cardList.title == null || cardList.parentBoard == null){
            return ResponseEntity.badRequest().build();
        }
        listRepo.saveAndFlush(cardList);
        return ResponseEntity.ok(cardList);
    }

    private static boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }
}