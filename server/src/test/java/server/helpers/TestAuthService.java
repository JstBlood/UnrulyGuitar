package server.helpers;

import commons.User;
import server.database.BoardRepository;
import server.database.UserRepository;
import server.services.RepositoryBasedAuthService;

public class TestAuthService extends RepositoryBasedAuthService {
    private UserRepository userRepo;
    private BoardRepository repo;

    private boolean doFail = false;

    public User toRetieve = new User("test");

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
        return toRetieve;
    }

    public boolean hasEditAccess(String username, String password, String bid) {
        return !doFail;
    }

    public boolean checkAdminPass(String password) { return !doFail; }
}
