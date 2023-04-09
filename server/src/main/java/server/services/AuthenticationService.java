package server.services;

import commons.User;

public interface AuthenticationService {
    User retriveUser(String username);
    boolean hasEditAccess(String username, String password, String bid);
}
