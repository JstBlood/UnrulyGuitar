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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.services.BoardsService;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping(value = {"/secure/{username}/{password}/boards", "/secure/{username}/boards"})
public class BoardsController {
    private final BoardsService service;

    public BoardsController(BoardsService service) {
        this.service = service;
    }

    @PostMapping("/add")
    public ResponseEntity<Board> add(@RequestBody Board board,
                                     @PathVariable String username,
                                     @PathVariable(required = false) String password) {
        var status = service.add(board, username, password);

        if(status != HttpStatus.OK)
            return ResponseEntity.status(status).build();

        return ResponseEntity.ok(service.getBoard(board.key));
    }

    @PostMapping("/join/{id}")
    public ResponseEntity<Board> join(@PathVariable String id,
                                      @PathVariable String username,
                                      @PathVariable(required = false) String password) {
        var status = service.join(id, username, password);

        if(status != HttpStatus.OK)
            return ResponseEntity.status(status).build();

        return ResponseEntity.ok(service.getBoard(id));
    }

    @PostMapping("/leave/{id}")
    public ResponseEntity<?> leave(@PathVariable String id,
                                   @PathVariable String username,
                                   @PathVariable(required = false) String password) {
        return ResponseEntity.status(service.leave(id, username, password)).build();
    }

    @PutMapping("/{id}/{component}")
    public ResponseEntity<?> update(@PathVariable String key, @PathVariable String component,
                                    @RequestBody String newValue, @PathVariable String username,
                                    @PathVariable(required = false) String password) {
        return ResponseEntity.status(service.update(key, component, newValue, username, password)).build();
    }

    @PutMapping("/{id}/title")
    public ResponseEntity<?> updateTitle(@PathVariable String key,
                                         @RequestBody String newValue,  @PathVariable String username,
                                         @PathVariable(required = false) String password) {
        return ResponseEntity.status(service.updateTitle(key, newValue, username, password)).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable String id,
                                    @PathVariable String username,
                                    @PathVariable(required = false) String password) {
        return ResponseEntity.status(service.delete(id, username, password)).build();
    }

    @GetMapping("/list")
    public ResponseEntity<List<Board>> all(@PathVariable String username,
                                           @PathVariable String password) {
        return service.getAll(username, password);
    }

    @GetMapping("/previous")
    public ResponseEntity<Set<Board>> previous(@PathVariable String username,
                                               @PathVariable String password) {
        return service.getPrev(username, password);
    }

    @GetMapping("/force_refresh/{id}")
    public ResponseEntity<?> previous(@PathVariable String id,
                                      @PathVariable String username,
                                      @PathVariable(required = false) String password) {
        return ResponseEntity.status(service.forceRefresh(id)).build();
    }
}