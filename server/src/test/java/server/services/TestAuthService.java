package server.services;

import commons.User;
import org.springframework.stereotype.Service;
import server.database.BoardRepository;
import server.database.UserRepository;

@Service
public class TestAuthService extends RepositoryBasedAuthService {
    private UserRepository userRepo;
    private BoardRepository repo;

    public TestAuthService() {
        super(null, null);
    }

    public User retriveUser(String username) {
        return new User("test");
    }

    public boolean hasEditAccess(String username, String password, String bid) {
        return true;
    }

    public static boolean checkAdminPass(String password) { return true; }
}
