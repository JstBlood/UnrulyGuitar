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
import commons.Quote;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import server.database.BoardsRepository;
import server.database.QuoteRepository;

import java.awt.*;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@RestController
public class BoardsController {
    private final Random random;
    private final BoardsRepository repo;
    private SimpMessagingTemplate messages;

    public BoardsController(Random rng, BoardsRepository repo, SimpMessagingTemplate messages) {
        this.random = rng;
        this.repo = repo;
        this.messages = messages;
    }

    @MessageMapping("/board/join")
    @SendTo("/topic/board")
    public Optional<Board> joinBoard(Long bid) {
        if(!repo.existsById(bid)) {
            System.out.println("not found");
            return Optional.empty();
        }

        System.out.println("joined " + bid);
        return Optional.of(repo.getById(bid));
    }

    @MessageMapping("/board/create")
    @SendTo("/topic/board")
    public Board createBoard(int i) {
        System.out.println("Creating board!");
        // TODO: Change this to actual id generation
        Board created = new Board(random.nextLong(), "New board", "", new Color(12));

        repo.save(created);
        return created;
    }
}