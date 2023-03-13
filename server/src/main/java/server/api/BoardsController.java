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

import java.util.*;

import commons.Board;
import commons.User;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import server.Config;
import server.database.BoardRepository;
import server.database.UserRepository;

@RestController
@RequestMapping("/api/boards")
public class BoardsController {
    private final Random random;
    private final BoardRepository repo;
    private final UserRepository userRepo;
    private SimpMessagingTemplate messages;

    public BoardsController(Random rng, BoardRepository repo, UserRepository userRepo, SimpMessagingTemplate messages) {
        this.random = rng;
        this.repo = repo;
        this.messages = messages;
        this.userRepo = userRepo;
    }

    private User handleUser(String username) {
        if(userRepo.findByUsername(username) == null)
            return new User(username);

        return userRepo.findByUsername(username);
    }

    @PostMapping("join")
    public ResponseEntity<Board> joinBoard(@RequestBody Pair<String, String> key) {
        User usr = handleUser(key.getKey());


        if(repo.findByKey(key.getValue()) == null) {
            return ResponseEntity.notFound().build();
        }

        Board joined = repo.findByKey(key.getValue());

        usr.boards.add(joined);
        System.out.println(usr.boards);

        return ResponseEntity.ok(joined);
    }

    @PostMapping("create")
    public Board createBoard(@RequestBody String uname) {
        User usr = handleUser(uname);


        // TODO: Change this to actual id generation
        Board created = new Board(Long.toString(random.nextLong()), "New board");

        System.out.println(usr.boards.add(created));

        System.out.println(usr.boards);
        System.out.println(usr);

        repo.save(created);
        return created;
    }

    @PostMapping("list")
    public ResponseEntity<List<Board>> getBoards(@RequestBody String adminPass) {
        if(!Config.getAdminPass().equals(adminPass))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        System.out.println(repo.findAll());

        return ResponseEntity.ok(repo.findAll());
    }

    @PostMapping("previous")
    public Set<Board> getPrev(@RequestBody String username) {
        User usr = handleUser(username);

        System.out.println(usr.boards.toString());

        return usr.boards;
    }
}