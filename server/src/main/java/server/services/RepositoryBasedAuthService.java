package server.services;

import commons.User;
import org.springframework.stereotype.Service;
import server.database.UserRepository;

@Service
public class RepositoryBasedAuthService implements AuthenticationService {
    private UserRepository userRepo;

    public RepositoryBasedAuthService(UserRepository uRepo) {
        userRepo = uRepo;
    }

    public User retriveUser(String username) {
        if(userRepo.findByUsername(username) == null)
            return new User(username);

        return userRepo.findByUsername(username);
    }

    public boolean hasEditAccess(String username, String bid) {
        // TODO: Herein we will check if the user can edit our board.

        return true;
    }

    public static boolean checkAdminPass(String password) { return password.equals("xyz"); }
}
