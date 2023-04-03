package server.services;

import java.util.Optional;

import commons.Task;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import server.database.TaskRepository;

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

        if(optionalTask == null) {
            return HttpStatus.NOT_FOUND;
        }

        Task task = optionalTask.get();

        taskRepo.deleteById(id);
        forceRefresh(task);

        return HttpStatus.OK;
    }

    @Override
    public HttpStatus update(Long id, String component, Object newValue, String username, String password) {
        return HttpStatus.BAD_REQUEST;
    }

    public HttpStatus updateTitle(Long id, Object newValue, String username, String password) {
        if (!prepare(id, username, password).equals(HttpStatus.OK))
            return prepare(id, username, password);

        Task task = taskRepo.findById(id).get();
        String newValueString = String.valueOf(newValue).trim();

        if(isNullOrEmpty(newValueString)) {
            return HttpStatus.BAD_REQUEST;
        }

        task.title = newValueString;

        return flush(task);
    }

    public HttpStatus updateIsDone(Long id, Object newValue, String username, String password) {
        if (!prepare(id, username, password).equals(HttpStatus.OK))
            return prepare(id, username, password);

        Task task = taskRepo.findById(id).get();
        boolean newValueBool = Boolean.parseBoolean(newValue.toString());

        task.isDone = newValueBool;

        return flush(task);
    }

    public HttpStatus updateIndex(Long id, Object newValue, String username, String password) {
        if (!prepare(id, username, password).equals(HttpStatus.OK))
            return prepare(id, username, password);

        Task task = taskRepo.findById(id).get();

        int newIndex = Integer.parseInt(String.valueOf(newValue));

        if (newIndex < 0) {
            return HttpStatus.BAD_REQUEST;
        }

        task.index = newIndex;

        return flush(task);
    }

    public HttpStatus prepare(Long id, String username, String password) {
        if (id < 0) {
            return HttpStatus.BAD_REQUEST;
        }

        Optional<Task> optionalTask = taskRepo.findById(id);

        if(optionalTask == null) {
            return HttpStatus.NOT_FOUND;
        }

        return HttpStatus.OK;
    }

    public HttpStatus flush(Task task) {
        taskRepo.saveAndFlush(task);
        forceRefresh(task);

        return HttpStatus.OK;
    }

    private void forceRefresh(Task task) {
        boards.forceRefresh(task.parentCard.parentCardList.parentBoard.key);
    }

    public Boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }
}
