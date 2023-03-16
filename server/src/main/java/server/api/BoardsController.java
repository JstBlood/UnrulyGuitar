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

import commons.Board;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.database.BoardRepository;

@RestController
@RequestMapping("/api/boards")
public class BoardsController {
    private final Random random;
    private final BoardRepository repo;
    private SimpMessagingTemplate messages;

    public BoardsController(Random rng, BoardRepository repo, SimpMessagingTemplate messages) {
        this.random = rng;
        this.repo = repo;
        this.messages = messages;
    }

    @PostMapping("/join")
    public ResponseEntity<Board> joinBoard(@RequestBody String key) {
        if(repo.findByKey(key) == null) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(repo.findByKey(key));
    }

    @PostMapping(path = "/create")
    public ResponseEntity<Board> addBoard(@RequestBody Board board) {
        if(board == null || isNullOrEmpty(board.key)) {
            return  ResponseEntity.badRequest().build();
        }

        Board saved = repo.save(board);

        return ResponseEntity.ok(saved);
    }

    private static boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }

}