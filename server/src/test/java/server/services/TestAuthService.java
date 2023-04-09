package server.services;

import commons.User;
import server.database.BoardRepository;
import server.database.UserRepository;

public class TestAuthService extends RepositoryBasedAuthService {
    private UserRepository userRepo;
    private BoardRepository repo;

    private boolean doFail = false;

    public void setFail() {
        doFail = true;
    }

    public void setNoFail() {
        doFail = false;
    }

    public TestAuthService() {
        super(null, null);
    }

    public User retriveUser(String username) {
        return new User("test");
    }

    public boolean hasEditAccess(String username, String password, String bid) {
        return !doFail;
    }

    public boolean checkAdminPass(String password) { return !doFail; }
}
