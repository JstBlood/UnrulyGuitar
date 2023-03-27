package server.services;

import java.util.Optional;

import commons.Task;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import server.database.TaskRepository;

@Service
public class TaskService implements StandardEntityService<Task, Long>{
    private final TaskRepository taskRepo;
    private final BoardsService boardsService;

    public TaskService(TaskRepository taskRepo, BoardsService boardsService) {
        this.taskRepo = taskRepo;
        this.boardsService = boardsService;
    }

    @Override
    public HttpStatus add(Task task, String username, String password) {
        if (task == null || task.parentCard == null|| isNullOrEmpty(task.title)) {
            return HttpStatus.BAD_REQUEST;
        }

        taskRepo.saveAndFlush(task);
        forceRefresh(task);

        return HttpStatus.OK;
    }

    @Override
    public HttpStatus update(Long id, String component, Object newValue, String username, String password) {
        Optional<Task> optionalTask = taskRepo.findById(id);

        if(optionalTask.isEmpty()) {
            return HttpStatus.NOT_FOUND;
        }

        Task task = optionalTask.get();

        if(isNullOrEmpty(task.title)) {
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

    @Override
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

    private void forceRefresh(Task task) {
        boardsService.forceRefresh(task.parentCard.parentCardList.parentBoard.key);
    }

    public Boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }
}
