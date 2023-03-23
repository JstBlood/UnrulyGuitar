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
import org.springframework.web.bind.annotation.*;
import server.database.BoardRepository;
import server.database.UserRepository;
import server.services.SocketRefreshService;
import server.services.RepositoryBasedAuthService;

@RestController
@RequestMapping("/api/boards")
public class BoardsController {
    private final BoardRepository repo;
    private final UserRepository userRepo;
    private final SocketRefreshService sockets;
    private final RepositoryBasedAuthService pwd;

    public BoardsController(BoardRepository repo, UserRepository userRepo,
                            SocketRefreshService messages, RepositoryBasedAuthService pwd) {

        this.repo = repo;
        this.sockets = messages;
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

        return ResponseEntity.ok(board);
    }

    @PostMapping("/restricted/{adminPass}/list")
    public ResponseEntity<List<Board>> getBoards(@PathVariable String adminPass) {
        if(!pwd.checkAdminPass(adminPass))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        return ResponseEntity.ok(repo.findAll());
    }

    @PostMapping("/secure/{username}/previous")
    public Set<Board> getPrev(@PathVariable String username) {
        User usr = pwd.retriveUser(username);

        return usr.boards;
    }

    @GetMapping("/{id}/forceRefresh")
    public ResponseEntity<String> forceRefresh(@PathVariable String id) {
        if(repo.findByKey(id) == null)
            return ResponseEntity.notFound().build();

        sockets.broadcast(repo.findByKey(id));

        return ResponseEntity.ok("");
    }

    private static boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }
}