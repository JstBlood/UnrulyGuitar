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
import commons.Card;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import server.database.BoardRepository;
import server.database.ListRepository;

import java.awt.*;
import java.util.Random;

@RestController
@RequestMapping("/api/list")
public class ListsController {
    private final Random random;
    private final ListRepository listRepo;
    private final BoardRepository boardRepo;
    private SimpMessagingTemplate messages;

    public ListsController(Random rng, ListRepository listRepo,
                           BoardRepository boardRepo, SimpMessagingTemplate messages) {
        this.random = rng;
        this.messages = messages;
        this.listRepo = listRepo;
        this.boardRepo = boardRepo;
    }

    @GetMapping("/{bid}/create")
    public Card createLists(@PathVariable("bid") String bid) {
        Board pBoard = boardRepo.findByKey(bid);
        Card created = new Card("add Title", "add Description", pBoard);
        pBoard.addCard(created);
        listRepo.save(created);
        return created;
    }

    @GetMapping("/{bid}/delete")
    public void deleteLists(long i, @PathVariable("bid") String bid) {
        Board pBoard = boardRepo.findByKey(bid);
        Card other = listRepo.getById(i);
        listRepo.delete(other);
        pBoard.cards.remove(other);
    }

    @GetMapping("/{bid}/update")
    public void updateLists(Card card, String title, String description) {
        card.setTitle(title);
        card.setDescription(description);
    }






}