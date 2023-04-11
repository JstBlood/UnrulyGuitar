package server.api;

import commons.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.services.TagService;

@RestController
@RequestMapping(value = {"/secure/{username}/{password}/tags", "/secure/{username}/tags"})
public class TagController {
    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @PostMapping("/add")
    public ResponseEntity<?> add(@RequestBody Tag tag, @PathVariable String username,
                                 @PathVariable(required = false) String password) {
        return ResponseEntity.status(tagService.add(tag, username, password)).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable long id, @PathVariable String username,
                                    @PathVariable(required = false) String password) {
        return ResponseEntity.status(tagService.delete(id, username, password)).build();
    }

    @PutMapping("/{id}/name")
    public ResponseEntity<?> updateName(@PathVariable long id,
                                        @RequestBody String newValue,
                                        @PathVariable String username,
                                        @PathVariable(required = false) String password) {
        return ResponseEntity.status(tagService.updateName(id, newValue, username, password)).build();
    }

    @PutMapping("/{id}/foreground")
    public ResponseEntity<?> updateForeground(@PathVariable long id,
                                         @RequestBody String newValue,
                                         @PathVariable String username,
                                         @PathVariable(required = false) String password) {
        return ResponseEntity.status(tagService.updateForeground(id, newValue, username, password)).build();
    }

    @PutMapping("/{id}/background")
    public ResponseEntity<?> updateBackground(@PathVariable long id,
                                              @RequestBody String newValue,
                                              @PathVariable String username,
                                              @PathVariable(required = false) String password) {
        return ResponseEntity.status(tagService.updateBackground(id, newValue, username, password)).build();
    }
}
