package server.services;

import commons.Task;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import server.database.TaskRepository;

import java.util.Objects;
import java.util.Optional;

@Service
public class TaskService implements StandardEntityService<Task, Long> {
    private final TaskRepository taskRepo;
    private final BoardsService boards;

    public TaskService(TaskRepository taskRepo, BoardsService boards) {
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

    @Override
    public HttpStatus update(Long id, String component, Object newValue, String username, String password) {

        Optional<Task> optionalTask = taskRepo.findById(id);

        if(optionalTask.isEmpty()) {
            return HttpStatus.NOT_FOUND;
        }

        Task task = optionalTask.get();

        HttpStatus res = handleSwitch(component, newValue, task);

        if (res.equals(HttpStatus.BAD_REQUEST))
            return res;

        taskRepo.saveAndFlush(task);

        forceRefresh(task);

        return res;
    }

    private HttpStatus handleSwitch(String component, Object newValue, Task task) {

        HttpStatus res = null;

        switch (component) {
            case "title":
                res = updateTitle(newValue, task);
                break;

            case "isDone":
                res = updateIsDone(newValue, task);
                break;

            default:
                res = HttpStatus.BAD_REQUEST;
                break;

        }

        return res;
    }

    private HttpStatus updateIsDone(Object newValue, Task task) {

        if(Objects.isNull(newValue)) {
            return HttpStatus.BAD_REQUEST;
        }

        var newValueBool = Boolean.parseBoolean(newValue.toString());

        task.isDone = newValueBool;

        return HttpStatus.OK;
    }

    private HttpStatus updateTitle(Object newValue, Task task) {

        if(Objects.isNull(newValue)) {
            return HttpStatus.BAD_REQUEST;
        }

        String newValueString = String.valueOf(newValue).trim();

        if(isNullOrEmpty(newValueString)) {
            return HttpStatus.BAD_REQUEST;
        }

        task.title = newValueString;

        return HttpStatus.OK;
    }

    private void forceRefresh(Task task) {
        boards.forceRefresh(task.parentCard.parentCardList.parentBoard.key);
    }

    public Boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }
}
