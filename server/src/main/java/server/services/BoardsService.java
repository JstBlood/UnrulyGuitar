package server.services;

import commons.Board;
import commons.Card;
import commons.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import server.database.BoardRepository;
import server.database.UserRepository;

import java.util.List;
import java.util.Optional;
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

    public HttpStatus leave(String id, String username, String password) {
        User usr = pwd.retriveUser(username);

        if(repo.findByKey(id) == null) {
            return HttpStatus.NOT_FOUND;
        }

        Board toBeLeft = repo.findByKey(id);

        usr.boards.removeIf(x -> x.id == toBeLeft.id);
        userRepo.saveAndFlush(usr);

        return HttpStatus.OK;
    }

    public HttpStatus update(String id, String component, Object newValue, String username, String password) {
//        if(repo.findByKey(id) == null)
//            return HttpStatus.NOT_FOUND;
//        if(!pwd.hasEditAccess(password, id))
//            return HttpStatus.FORBIDDEN;
//
//        Board board = repo.findByKey(id);
//        String newValueString = String.valueOf(newValue).trim();
//
//        if(newValueString.isEmpty()) {
//            return HttpStatus.BAD_REQUEST;
//        }
//
//        repo.save(edit);
//
//        forceRefresh(id);
//
//        return HttpStatus.OK;

        //Unused, delete?
        return HttpStatus.NO_CONTENT;
    }

    public HttpStatus updateTitle(long id, String newValue, String username, String password) {
        if (!prepare(id, username, password).equals(HttpStatus.OK))
            return prepare(id, username, password);

        Board board = repo.findById(id).get();
        String newValueString = String.valueOf(newValue).trim();

        if(newValueString.isEmpty()) {
            return HttpStatus.BAD_REQUEST;
        }

        board.title = newValueString;

        return flush(board);
    }

    public HttpStatus prepare(long id, String username, String password) {
        Optional<Board> optionalBoard = repo.findById(id);

        if(optionalBoard.isEmpty()) {
            return HttpStatus.NOT_FOUND;
        }

        if(!pwd.hasEditAccess(password, String.valueOf(id))) {
            return HttpStatus.FORBIDDEN;
        }

        return HttpStatus.OK;
    }

    public HttpStatus flush(Board board) {
        repo.save(board);
        forceRefresh(board.key);

        return HttpStatus.OK;
    }

    public HttpStatus delete(String id, String username, String password) {
        if(repo.findByKey(id) == null)
            return HttpStatus.NOT_FOUND;

        Board rem = repo.findByKey(id);

        for(User usr : rem.users) {
            Board fRem = rem;
            usr.boards.removeIf(x -> x.id == fRem.id);
            userRepo.saveAndFlush(usr);
        }

        repo.delete(rem);

        sockets.broadcastRemoval(rem);

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
