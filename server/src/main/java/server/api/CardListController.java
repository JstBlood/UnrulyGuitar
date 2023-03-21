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

import java.util.Random;

import commons.CardList;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.database.CardListRepository;
import server.security.PasswordValidator;

@RestController
@RequestMapping("/api/cardlists")
public class CardListController {
    private final Random random;
    private final CardListRepository listRepo;
    private BoardsController boardsController;
    private PasswordValidator pwd;

    public CardListController(Random rng, CardListRepository listRepo,
                              BoardsController boardsController, PasswordValidator pwd) {
        this.random = rng;
        this.listRepo = listRepo;
        this.boardsController = boardsController;
        this.pwd = pwd;
    }

    @PostMapping("/add")
    public ResponseEntity<CardList> add(@RequestBody CardList cardList) {
        if (cardList == null || isNullOrEmpty(cardList.title) || cardList.parentBoard == null) {
            return ResponseEntity.badRequest().build();
        }
        CardList saved = listRepo.save(cardList);

        boardsController.forceRefresh(cardList.parentBoard.key);

        return ResponseEntity.ok(saved);
    }

    @PostMapping("/restricted/{password}/delete")
    public ResponseEntity<CardList> delete(@RequestBody CardList cardList) {
        if (cardList == null || cardList.parentBoard == null) {
            return ResponseEntity.badRequest().build();
        }
        listRepo.deleteById(cardList.id);
        boardsController.forceRefresh(cardList.parentBoard.key);
        return ResponseEntity.ok(cardList);
    }

    @PutMapping("/restricted/{password}/{id}/edit/{component}")
    public ResponseEntity<String> update(@RequestBody String newValue, @PathVariable String password,
                                           @PathVariable long id, @PathVariable String component) {
        if(listRepo.findById(id) == null)
            return ResponseEntity.notFound().build();

        CardList edit = listRepo.findById(id);

        if(!pwd.hasEditAccess(password, edit.parentBoard.key))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        try {
            edit.getClass().getField(component).set(edit, newValue);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }

        listRepo.save(edit);

        boardsController.forceRefresh(edit.parentBoard.key);

        return ResponseEntity.ok("");
    }

    private static boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }
}