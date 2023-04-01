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

    @PutMapping("/{id}/{component}")
    public ResponseEntity<?> update(@PathVariable long id, @PathVariable String component,
                                    @RequestBody Object newValue, @PathVariable String username,
                                    @PathVariable(required = false) String password) {
        return ResponseEntity.status(tagService.update(id, component, newValue, username, password)).build();
    }
}
