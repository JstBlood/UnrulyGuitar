package server.api;

import commons.Task;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.database.TaskRepository;
import server.services.BoardsService;

import java.util.Random;

@RestController
@RequestMapping("/api/tasks/")
public class TaskController {
    private final Random random;
    private final TaskRepository taskRepo;
    private BoardsService boards;

    public TaskController(Random rng, TaskRepository taskRepo, BoardsService boards) {
        this.random = rng;
        this.taskRepo = taskRepo;
        this.boards = boards;
    }
    @PostMapping("/secure/{username}/{id}/add")
    public ResponseEntity<Task> add(@RequestBody Task task) {
        if (task == null || isNullOrEmpty(task.title) || task.parentCard == null) {
            return ResponseEntity.badRequest().build();
        }
        Task saved = taskRepo.save(task);

        boards.forceRefresh(task.parentCard.parentCardList.parentBoard.key);

        return ResponseEntity.ok(saved);
    }

    @PostMapping("/secure/{username}/{id}/delete")
    public ResponseEntity<Task> delete(@RequestBody Task task) {
        if (task == null || task.parentCard == null) {
            return ResponseEntity.badRequest().build();
        }
        taskRepo.deleteById(task.id);

        boards.forceRefresh(task.parentCard.parentCardList.parentBoard.key);

        return ResponseEntity.ok(task);
    }

    @PutMapping("/secure/{username}/{id}/update")
    public ResponseEntity<Task> update(@RequestBody Task task) {
        if (task == null || task.title == null || task.parentCard == null){
            return ResponseEntity.badRequest().build();
        }
        taskRepo.saveAndFlush(task);

        boards.forceRefresh(task.parentCard.parentCardList.parentBoard.key);

        return ResponseEntity.ok(task);
    }

    private static boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }
}
