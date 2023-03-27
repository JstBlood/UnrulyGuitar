package server.services;

import org.springframework.http.HttpStatus;

public interface StandardEntityService<T, U> {
    HttpStatus add(T newComponent, String username, String password);
    HttpStatus update(U id, String component, Object newValue, String username, String password);
    HttpStatus delete(U id, String username, String password);
}
