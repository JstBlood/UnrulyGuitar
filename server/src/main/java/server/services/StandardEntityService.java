package server.services;

import org.springframework.http.HttpStatus;

public interface StandardEntityService<T> {
    HttpStatus add(T newComponent);
    HttpStatus update(long id, String component, Object newValue);
    HttpStatus delete(long id);
}
