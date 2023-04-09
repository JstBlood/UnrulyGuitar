package server.services;

import commons.Board;
import commons.User;
import org.springframework.stereotype.Service;
import server.database.BoardRepository;
import server.database.UserRepository;

@Service
public class RepositoryBasedAuthService implements AuthenticationService {
    private UserRepository userRepo;
    private BoardRepository repo;

    public RepositoryBasedAuthService(UserRepository uRepo, BoardRepository repo) {
        this.repo = repo;
        userRepo = uRepo;
    }

    public User retriveUser(String username) {
        if(userRepo.findByUsername(username) == null)
            return new User(username);

        return userRepo.findByUsername(username);
    }

    public boolean hasEditAccess(String username, String password, String bid) {
        if(username.endsWith("_admin"))
            return checkAdminPass(password);

        Board board = repo.findByKey(bid);
        if(board == null)
            return false;

        if(!board.isPasswordProtected)
            return true;

        if(board.password.equals(password))
            return true;

        return false;
    }

    public boolean checkAdminPass(String password) { return password.equals("xyz"); }
}
