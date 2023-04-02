package server.api;

import commons.Task;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.services.TaskService;

@RestController
@RequestMapping(value = {"/secure/{username}/{password}/tasks", "/secure/{username}/tasks"})
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping("/add")
    public ResponseEntity<?> add(@RequestBody Task task, @PathVariable String username,
                                 @PathVariable(required = false) String password) {
        return ResponseEntity.status(taskService.add(task, username, password)).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable long id, @PathVariable String username,
                                    @PathVariable(required = false) String password) {
        return ResponseEntity.status(taskService.delete(id, username, password)).build();
    }

    @PutMapping("/{id}/{component}")
    public ResponseEntity<?> update(@PathVariable long id, @PathVariable String component,
                                    @RequestBody Object newValue, @PathVariable String username,
                                    @PathVariable(required = false) String password) {
        return ResponseEntity.status(taskService.update(id, component, newValue, username, password)).build();
    }

    @PutMapping("/{id}/title")
    public ResponseEntity<?> updateTitle(@PathVariable long id,
                                         @RequestBody String newValue, @PathVariable String username,
                                         @PathVariable(required = false) String password) {
        return ResponseEntity.status(taskService.updateTitle(id, newValue, username, password)).build();
    }

    @PutMapping("/{id}/isDone")
    public ResponseEntity<?> updateIsDone(@PathVariable long id,
                                          @RequestBody Object newValue, @PathVariable String username,
                                          @PathVariable(required = false) String password) {
        return ResponseEntity.status(taskService.updateIsDone(id, newValue, username, password)).build();
    }

    @PutMapping("/{id}/index")
    public ResponseEntity<?> updateIndex(@PathVariable long id,
                                          @RequestBody Object newValue, @PathVariable String username,
                                          @PathVariable(required = false) String password) {
        return ResponseEntity.status(taskService.updateIndex(id, newValue, username, password)).build();
    }
}
