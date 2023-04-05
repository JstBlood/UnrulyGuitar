package server.services;

import commons.Board;
import commons.ColorPreset;
import commons.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import server.database.BoardRepository;
import server.database.ColorPresetRepository;
import server.database.UserRepository;

import java.util.List;
import java.util.Set;

@Service
public class BoardsService implements StandardEntityService<Board, String> {
    private final BoardRepository repo;
    private final UserRepository userRepo;
    private final ColorPresetRepository colorRepo;
    private final SocketRefreshService sockets;
    private final RepositoryBasedAuthService pwd;

    public BoardsService(BoardRepository repo,
                         UserRepository userRepo,
                         SocketRefreshService messages,
                         RepositoryBasedAuthService pwd,
                         ColorPresetRepository colorRepo) {

        this.repo = repo;
        this.sockets = messages;
        this.userRepo = userRepo;
        this.pwd = pwd;
        this.colorRepo = colorRepo;
    }

    public HttpStatus add(Board board, String username, String password) {
        if(board == null || isNullOrEmpty(board.key)) {
            return HttpStatus.BAD_REQUEST;
        }

        User usr = pwd.retriveUser(username);

        board.cardListColors = new ColorPreset();
        colorRepo.saveAndFlush(board.cardListColors);

        board.colors = new ColorPreset();
        colorRepo.saveAndFlush(board.colors);

        board.cardPresets.add(new ColorPreset());
        colorRepo.saveAndFlush(board.cardPresets.get(0));
        board.defaultPreset = board.cardPresets.get(0);

        usr.boards.add(board);

        repo.saveAndFlush(board);
        userRepo.saveAndFlush(usr);

        return HttpStatus.OK;
    }

    public HttpStatus join(String key, String username, String password) {
        User usr = pwd.retriveUser(username);

        if (isNullOrEmpty(key)) {
            return HttpStatus.BAD_REQUEST;
        }

        if (repo.findByKey(key) == null) {
            return HttpStatus.NOT_FOUND;
        }

        Board joined = repo.findByKey(key);

        joined.users.add(usr);

        repo.saveAndFlush(joined);

        usr.boards.add(joined);
        userRepo.saveAndFlush(usr);

        return HttpStatus.OK;
    }

    public HttpStatus leave(String key, String username, String password) {
        User usr = pwd.retriveUser(username);

        if (isNullOrEmpty(key)) {
            return HttpStatus.BAD_REQUEST;
        }

        if(repo.findByKey(key) == null) {
            return HttpStatus.NOT_FOUND;
        }

        Board toBeLeft = repo.findByKey(key);

        usr.boards.removeIf(x -> x.id == toBeLeft.id);
        userRepo.saveAndFlush(usr);

        return HttpStatus.OK;
    }

    public HttpStatus addColorPreset(String key, ColorPreset preset, String username, String password) {
        if (!prepare(key, username, password).equals(HttpStatus.OK))
            return prepare(key, username, password);

        Board board = repo.findByKey(key);

        colorRepo.saveAndFlush(preset);

        board.cardPresets.add(preset);

        return flush(board);
    }

    public HttpStatus removeColorPreset(String key, Long preset, String username, String password) {
        if (!prepare(key, username, password).equals(HttpStatus.OK))
            return prepare(key, username, password);

        Board board = repo.findByKey(key);

        int idx = -1;
        for(int i = 0; i < board.cardPresets.size(); i++) {
            if(board.cardPresets.get(i).id == preset) {
                idx = i;
                break;
            }
        }

        if(idx == -1)
            return HttpStatus.NOT_FOUND;

        board.cardPresets.remove(idx);

        return flush(board);
    }

    public HttpStatus updateForegroundPreset(String key, Long id, String newValue, String username, String password) {
        if (!prepare(key, username, password).equals(HttpStatus.OK))
            return prepare(key, username, password);

        Board b = repo.findByKey(key);

        if(colorRepo.findById(id).isEmpty())
            return HttpStatus.NOT_FOUND;

        ColorPreset preset = colorRepo.findById(id).get();

        if(newValue.isEmpty()) {
            return HttpStatus.BAD_REQUEST;
        }

        preset.foreground = newValue;

        colorRepo.saveAndFlush(preset);

        return flush(b);
    }

    public HttpStatus updateBackgroundPreset(String key, Long id, String newValue, String username, String password) {
        if (!prepare(key, username, password).equals(HttpStatus.OK))
            return prepare(key, username, password);

        Board b = repo.findByKey(key);

        if(colorRepo.findById(id).isEmpty())
            return HttpStatus.NOT_FOUND;

        ColorPreset preset = colorRepo.findById(id).get();

        if(newValue.isEmpty()) {
            return HttpStatus.BAD_REQUEST;
        }

        preset.background = newValue;

        colorRepo.saveAndFlush(preset);

        return flush(b);
    }

    public HttpStatus updateDefaultPreset(String key, Long id, String username, String password) {
        if (!prepare(key, username, password).equals(HttpStatus.OK))
            return prepare(key, username, password);

        Board b = repo.findByKey(key);

        if(colorRepo.findById(id).isEmpty())
            return HttpStatus.NOT_FOUND;

        ColorPreset preset = colorRepo.findById(id).get();


        b.defaultPreset = preset;

        repo.saveAndFlush(b);

        return flush(b);
    }

    public HttpStatus updateBackground(String key, String newValue, String username, String password) {
        if (!prepare(key, username, password).equals(HttpStatus.OK))
            return prepare(key, username, password);

        Board board = repo.findByKey(key);

        if(newValue.isEmpty()) {
            return HttpStatus.BAD_REQUEST;
        }

        board.colors.background = newValue;

        return flush(board);
    }


    public HttpStatus updateForeground(String key, String newValue, String username, String password) {
        if (!prepare(key, username, password).equals(HttpStatus.OK))
            return prepare(key, username, password);

        Board board = repo.findByKey(key);

        if(newValue.isEmpty()) {
            return HttpStatus.BAD_REQUEST;
        }

        board.colors.foreground = newValue;

        return flush(board);
    }

    public HttpStatus updateBackgroundList(String key, String newValue, String username, String password) {
        if (!prepare(key, username, password).equals(HttpStatus.OK))
            return prepare(key, username, password);

        Board board = repo.findByKey(key);

        if(newValue.isEmpty()) {
            return HttpStatus.BAD_REQUEST;
        }

        board.cardListColors.background = newValue;

        return flush(board);
    }


    public HttpStatus updateForegroundList(String key, String newValue, String username, String password) {
        if (!prepare(key, username, password).equals(HttpStatus.OK))
            return prepare(key, username, password);

        Board board = repo.findByKey(key);

        if(newValue.isEmpty()) {
            return HttpStatus.BAD_REQUEST;
        }

        board.cardListColors.foreground = newValue;

        return flush(board);
    }

    public HttpStatus updateTitle(String key, String newValue, String username, String password) {
        if (!prepare(key, username, password).equals(HttpStatus.OK))
            return prepare(key, username, password);

        Board board = repo.findByKey(key);

        if(isNullOrEmpty(newValue)) {
            return HttpStatus.BAD_REQUEST;
        }

        String newValueString = String.valueOf(newValue).trim();

        board.title = newValueString;

        return flush(board);
    }

    public HttpStatus prepare(String key, String username, String password) {
        if (isNullOrEmpty(key)) {
            return HttpStatus.BAD_REQUEST;
        }

        Board optionalBoard = repo.findByKey(key);

        if(optionalBoard == null) {
            return HttpStatus.NOT_FOUND;
        }

        if(!pwd.hasEditAccess(password, key)) {
            return HttpStatus.FORBIDDEN;
        }

        return HttpStatus.OK;
    }

    public HttpStatus flush(Board board) {
        repo.save(board);
        forceRefresh(board.key);

        return HttpStatus.OK;
    }

    public HttpStatus delete(String key, String username, String password) {
        if (isNullOrEmpty(key)) {
            return HttpStatus.BAD_REQUEST;
        }

        if(repo.findByKey(key) == null)
            return HttpStatus.NOT_FOUND;

        Board rem = repo.findByKey(key);

        for(User usr : rem.users) {
            Board fRem = rem;
            usr.boards.removeIf(x -> x.key.equals(fRem.key));
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

    public Board getBoard(String key) {
        return repo.findByKey(key);
    }

    public HttpStatus forceRefresh(String key) {
        if (isNullOrEmpty(key)) {
            return HttpStatus.BAD_REQUEST;
        }

        if(repo.findByKey(key) == null) {
            return HttpStatus.NOT_FOUND;
        }

        sockets.broadcast(repo.findByKey(key));

        return HttpStatus.OK;
    }

    private boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }
}
