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
import commons.ColorPreset;
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

    /**
     * server method for adding boards to the database
     * @param board board to add
     * @param username of the adder
     * @param password of the board
     * @return ResponseEntity status
     */
    @PostMapping("/add")
    public ResponseEntity<Board> add(@RequestBody Board board,
                                     @PathVariable String username,
                                     @PathVariable(required = false) String password) {
        var status = service.add(board, username, password);

        if(status != HttpStatus.OK)
            return ResponseEntity.status(status).build();

        return ResponseEntity.ok(service.getBoard(board.key));
    }

    /**
     * adds a preset (colour) to the board
     * @param preset new colour preset
     * @param username of the adder
     * @param key of the board
     * @param password of the board
     * @return ResponseEntity status
     */
    @PostMapping("/{key}/addColorPreset")
    public ResponseEntity<?> addPreset(@RequestBody ColorPreset preset,
                                     @PathVariable String username, @PathVariable String key,
                                     @PathVariable(required = false) String password) {
        return ResponseEntity.status(service.addColorPreset(key, preset, username, password)).build();
    }

    /**
     * joins a user to a board
     * @param key of the board
     * @param username of the user
     * @param password of the board
     * @return ResponseEntity status
     */
    @PostMapping("/join/{key}")
    public ResponseEntity<Board> join(@PathVariable String key,
                                      @PathVariable String username,
                                      @PathVariable(required = false) String password) {
        var status = service.join(key, username, password);

        if(status != HttpStatus.OK)
            return ResponseEntity.status(status).build();

        return ResponseEntity.ok(service.getBoard(key));
    }

    @GetMapping("/{key}/validate")
    public ResponseEntity<?> validate(@PathVariable String key,
                                   @PathVariable String username,
                                   @PathVariable(required = false) String password) {
        return ResponseEntity.status(service.validate(key, username, password)).build();
    }

    /**
     * changes the password of a board
     * @param key of the board
     * @param username of the user
     * @param newValue new password
     * @param password old password
     * @return ResponseEntity status
     */
    @PutMapping("/{key}/password")
    public ResponseEntity<?> changePass(@PathVariable String key,
                                      @PathVariable String username,@RequestBody String newValue,
                                      @PathVariable(required = false) String password) {
        return ResponseEntity.status(service.changePass(key, newValue, username, password)).build();
    }

    /**
     * removes the password from a board
     * @param key of the board
     * @param username of the user
     * @param password old password of the board
     * @return ResponseEntity status
     */
    @DeleteMapping("/{key}/password")
    public ResponseEntity<?> removePass(@PathVariable String key,
                                        @PathVariable String username,
                                        @PathVariable(required = false) String password) {
        return ResponseEntity.status(service.removePass(key, username, password)).build();
    }

    /**
     * leaves a user from a board
     * @param key of the board
     * @param username of the user
     * @param password of the board
     * @return ResponseEntity status
     */
    @PostMapping("/leave/{key}")
    public ResponseEntity<?> leave(@PathVariable String key,
                                   @PathVariable String username,
                                   @PathVariable(required = false) String password) {
        return ResponseEntity.status(service.leave(key, username, password)).build();
    }

    /**
     * updates foreground colour
     * @param key of the board
     * @param newValue new colour value
     * @param username of the user
     * @param password of the board
     * @return ResponseEntity status
     */
    @PutMapping("/{key}/foreground")
    public ResponseEntity<?> updateFore(@PathVariable String key,
                                    @RequestBody String newValue, @PathVariable String username,
                                    @PathVariable(required = false) String password) {
        return ResponseEntity.status(service.updateForeground(key, newValue, username, password)).build();
    }

    /**
     * Updates the background colour
     * @param key of the board
     * @param newValue new colour value
     * @param username of the user
     * @param password of the board
     * @return ResponseEntity status
     */
    @PutMapping("/{key}/background")
    public ResponseEntity<?> updateBack(@PathVariable String key,
                                        @RequestBody String newValue, @PathVariable String username,
                                        @PathVariable(required = false) String password) {
        return ResponseEntity.status(service.updateBackground(key, newValue, username, password)).build();
    }

    /**
     * updates the list foreground colour
     * @param key of the board
     * @param newValue new colour value
     * @param username of the user
     * @param password of the board
     * @return ResponseEntity status
     */
    @PutMapping("/{key}/list/foreground")
    public ResponseEntity<?> updateForeList(@PathVariable String key,
                                        @RequestBody String newValue, @PathVariable String username,
                                        @PathVariable(required = false) String password) {
        return ResponseEntity.status(service.updateForegroundList(key, newValue, username, password)).build();
    }

    /**
     * updates background colour of lists
     * @param key of the board
     * @param newValue new colour value
     * @param username of the user
     * @param password of the board
     * @return ResponseEntity status
     */
    @PutMapping("/{key}/list/background")
    public ResponseEntity<?> updateBackList(@PathVariable String key,
                                        @RequestBody String newValue, @PathVariable String username,
                                        @PathVariable(required = false) String password) {
        return ResponseEntity.status(service.updateBackgroundList(key, newValue, username, password)).build();
    }

    /**
     * updates the preset for foreground colour
     * @param key of the board
     * @param id of the preset
     * @param newValue new colour value
     * @param username of the user
     * @param password of the board
     * @return ResponseEntity status
     */
    @PutMapping("/{key}/preset/{id}/foreground")
    public ResponseEntity<?> updateForePreset(@PathVariable String key, @PathVariable Long id,
                                            @RequestBody String newValue, @PathVariable String username,
                                            @PathVariable(required = false) String password) {
        return ResponseEntity.status(service.updateForegroundPreset(key, id, newValue, username, password)).build();
    }

    /**
     * updates background colour preset
     * @param key of the board
     * @param id of the preset
     * @param newValue new colour value
     * @param username of the user
     * @param password of the board
     * @return ResponseEntity status
     */
    @PutMapping("/{key}/preset/{id}/background")
    public ResponseEntity<?> updateBackPreset(@PathVariable String key, @PathVariable Long id,
                                              @RequestBody String newValue, @PathVariable String username,
                                              @PathVariable(required = false) String password) {
        return ResponseEntity.status(service.updateBackgroundPreset(key, id, newValue, username, password)).build();
    }

    @PutMapping("/{key}/defaultPreset")
    public ResponseEntity<?> updateDefPreset(@RequestBody Long id, @PathVariable String key
            , @PathVariable String username, @PathVariable(required = false) String password) {
        return ResponseEntity.status(service.updateDefaultPreset(key, id, username, password)).build();
    }


    @PutMapping("/{key}/title")
    public ResponseEntity<?> updateTitle(@PathVariable String key,
                                         @RequestBody String newValue,  @PathVariable String username,
                                         @PathVariable(required = false) String password) {
        return ResponseEntity.status(service.updateTitle(key, newValue, username, password)).build();
    }

    @DeleteMapping("/{key}/removeColorPreset/{id}")
    public ResponseEntity<?> removePreset(@PathVariable Long id,
                                       @PathVariable String username, @PathVariable String key,
                                       @PathVariable(required = false) String password) {
        return ResponseEntity.status(service.removeColorPreset(key, id, username, password)).build();
    }

    @DeleteMapping("/{key}")
    public ResponseEntity<?> delete(@PathVariable String key,
                                    @PathVariable String username,
                                    @PathVariable(required = false) String password) {
        return ResponseEntity.status(service.delete(key, username, password)).build();
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

    @GetMapping("/force_refresh/{key}")
    public ResponseEntity<?> previous(@PathVariable String key,
                                      @PathVariable String username,
                                      @PathVariable(required = false) String password) {
        return ResponseEntity.status(service.forceRefresh(key)).build();
    }
}