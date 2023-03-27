package server.services;

import commons.Board;
import commons.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import server.database.BoardRepository;
import server.database.UserRepository;

import java.util.List;
import java.util.Set;

@Service
public class BoardsService implements StandardEntityService<Board, String> {
    private final BoardRepository repo;
    private final UserRepository userRepo;
    private final SocketRefreshService sockets;
    private final RepositoryBasedAuthService pwd;

    public BoardsService(BoardRepository repo, UserRepository userRepo,
                            SocketRefreshService messages, RepositoryBasedAuthService pwd) {

        this.repo = repo;
        this.sockets = messages;
        this.userRepo = userRepo;
        this.pwd = pwd;
    }

    public HttpStatus add(Board board, String username, String password) {
        if(board == null || isNullOrEmpty(board.key)) {
            return HttpStatus.BAD_REQUEST;
        }

        User usr = pwd.retriveUser(username);

        usr.boards.add(board);

        repo.saveAndFlush(board);
        userRepo.saveAndFlush(usr);

        return HttpStatus.OK;
    }

    public HttpStatus join(String id, String username, String password) {
        User usr = pwd.retriveUser(username);

        if(repo.findByKey(id) == null) {
            return HttpStatus.NOT_FOUND;
        }

        Board joined = repo.findByKey(id);

        joined.users.add(usr);

        repo.saveAndFlush(joined);

        usr.boards.add(joined);
        userRepo.saveAndFlush(usr);

        return HttpStatus.OK;
    }

    public HttpStatus update(String id, String component, Object newValue, String username, String password) {
        if(repo.findByKey(id) == null)
            return HttpStatus.NOT_FOUND;
        if(!pwd.hasEditAccess(password, id))
            return HttpStatus.FORBIDDEN;

        Board edit = repo.findByKey(id);

        try {
            edit.getClass().getField(component).set(edit, newValue);
        } catch (Exception e) {
            return HttpStatus.BAD_REQUEST;
        }

        repo.save(edit);

        forceRefresh(id);

        return HttpStatus.OK;
    }

    public HttpStatus delete(String id, String username, String password) {
        if(repo.findByKey(id) == null)
            return HttpStatus.NOT_FOUND;

        String rem = repo.findByKey(id).key;

        repo.delete(repo.findByKey(id));
        forceRefresh(rem);

        return HttpStatus.OK;
    }

    public ResponseEntity<List<Board>> getAll(String username, String password) {
        if(!pwd.checkAdminPass(password))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        return ResponseEntity.ok(repo.findAll());
    }

    public ResponseEntity<Set<Board>> getPrev(String username, String password) {
        User usr = pwd.retriveUser(username);

        return ResponseEntity.ok(usr.boards);
    }

    public Board getBoard(String id) {
        return repo.findByKey(id);
    }

    public HttpStatus forceRefresh(String id) {
        if(repo.findByKey(id) == null)
            return HttpStatus.NOT_FOUND;

        sockets.broadcast(repo.findByKey(id));

        return HttpStatus.OK;
    }

    private boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }
}
