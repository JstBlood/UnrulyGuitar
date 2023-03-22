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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import server.database.BoardRepository;
import server.database.UserRepository;
import server.services.RepositoryBasedAuthService;

@RestController
@RequestMapping("/api/boards")
public class BoardsController {
    private final Random random;
    private final BoardRepository repo;
    private final UserRepository userRepo;
    private final RepositoryBasedAuthService pwd;
    private SimpMessagingTemplate messages;

    public BoardsController(Random rng, BoardRepository repo, UserRepository userRepo,
                            SimpMessagingTemplate messages, RepositoryBasedAuthService pwd) {
        this.random = rng;
        this.repo = repo;
        this.messages = messages;
        this.userRepo = userRepo;
        this.pwd = pwd;
    }

    @PostMapping("/secure/{username}/{id}/join")
    public ResponseEntity<Board> joinBoard(@PathVariable String username, @PathVariable String id) {
        User usr = pwd.retriveUser(username);


        if(repo.findByKey(id) == null) {
            return ResponseEntity.notFound().build();
        }

        Board joined = repo.findByKey(id);

        usr.boards.add(joined);
        userRepo.save(usr);

        // refetch the board with all new changes
        joined = repo.findByKey(id);
        stubRecurrence(joined);

        return ResponseEntity.ok(joined);
    }

    @PostMapping("/restricted/{password}/{id}/edit/{component}")
    public ResponseEntity<String> editBoard(@PathVariable String password, @PathVariable String id,
                                            @PathVariable String component, @RequestBody String newValue) {
        if(repo.findByKey(id) == null)
            return ResponseEntity.notFound().build();
        if(!pwd.hasEditAccess(password, id))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        Board edit = repo.findByKey(id);

        try {
            edit.getClass().getField(component).set(edit, newValue);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }

        repo.save(edit);

        forceRefresh(id);

        return ResponseEntity.ok("");
    }

    @PostMapping("/secure/{uname}/create")
    public ResponseEntity<Board> addBoard(@RequestBody Board board, @PathVariable String uname) {
        if(board == null || isNullOrEmpty(board.key)) {
            return ResponseEntity.badRequest().build();
        }

        User usr = pwd.retriveUser(uname);

        usr.boards.add(board);

        repo.save(board);
        userRepo.save(usr);

        stubRecurrence(board);

        return ResponseEntity.ok(board);
    }

    @PostMapping("/restricted/{adminPass}/list")
    public ResponseEntity<List<Board>> getBoards(@PathVariable String adminPass) {
        if(!pwd.checkAdminPass(adminPass))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        var stuber = repo.findAll();
        stubRecurrence(stuber);

        return ResponseEntity.ok(stuber);
    }

    @PostMapping("/secure/{username}/previous")
    public Set<Board> getPrev(@PathVariable String username) {
        User usr = pwd.retriveUser(username);

        var stuber = usr.boards;
        stubRecurrence(stuber);

        return stuber;
    }

    @GetMapping("/{id}/forceRefresh")
    public ResponseEntity<String> forceRefresh(@PathVariable String id) {
        if(repo.findByKey(id) == null)
            return ResponseEntity.notFound().build();

        var stuber = repo.findByKey(id);
        stubRecurrence(stuber);

        messages.convertAndSend("/topic/board/" + id, stuber);

        return ResponseEntity.ok("");
    }

    private static boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }

    // The boards have a field called users
    // The objects in that field (Users) have a field called boards
    // These boards will have pointers to the parent board
    // this is a recursive relation so if we want to send these objects over
    // json we need to stub those recurrences, this is what the following
    // functions do.
    private static void stubRecurrence(Collection<Board> boards) {
        for(var b : boards)
            stubRecurrence(b);
    }

    private static void stubRecurrence(Board board) {
        for(var u : board.users)
            u.boards = null;

        for(var p : board.cardLists) {
            p.parentBoard = null;

            for(var c : p.cards) {
                c.parentCardList = null;
            }
        }
    }
}