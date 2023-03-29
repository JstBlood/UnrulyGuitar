package server.services;

import commons.Task;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import server.database.CardRepository;
import server.database.TaskRepository;

import java.util.Optional;

@Service
public class TaskService implements StandardEntityService<Task, Long> {
    private final TaskRepository taskRepo;
    private final CardRepository cardRepo;
    private final BoardsService boards;

    public TaskService(CardRepository cardRepo, TaskRepository taskRepo, BoardsService boards) {
        this.cardRepo = cardRepo;
        this.taskRepo = taskRepo;
        this.boards = boards;
    }

    public HttpStatus add(Task task, String username, String password) {
        if (task == null || task.parentCard == null || isNullOrEmpty(task.title)) {
            return HttpStatus.BAD_REQUEST;
        }

        taskRepo.save(task);
        forceRefresh(task);

        return HttpStatus.CREATED;
    }

    public HttpStatus delete(Long id, String username, String password) {
        Optional<Task> optionalTask = taskRepo.findById(id);

        if(optionalTask.isEmpty()) {
            return HttpStatus.NOT_FOUND;
        }

        Task task = optionalTask.get();

        taskRepo.deleteById(id);
        forceRefresh(task);

        return HttpStatus.OK;
    }

    public HttpStatus update(Long id, String component, Object newValue, String username, String password) {
        Optional<Task> optionalTask = taskRepo.findById(id);

        if(optionalTask.isEmpty()) {
            return HttpStatus.NOT_FOUND;
        }

        Task task = optionalTask.get();

        if(newValue == null) {
            return HttpStatus.BAD_REQUEST;
        }

        try {
            task.getClass().getField(component).set(task, newValue);
        } catch (Exception e) {
            return HttpStatus.BAD_REQUEST;
        }

        taskRepo.saveAndFlush(task);
        forceRefresh(task);

        return HttpStatus.OK;
    }

    private void forceRefresh(Task task) {
        boards.forceRefresh(task.parentCard.parentCardList.parentBoard.key);
    }

    private boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }
}
