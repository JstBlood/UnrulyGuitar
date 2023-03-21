package server.security;

import commons.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import server.database.UserRepository;

@Configuration
public class PasswordValidator {
    private UserRepository userRepo;

    public PasswordValidator(UserRepository uRepo) {
        userRepo = uRepo;
    }

    public User handleUser(String username) {
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
