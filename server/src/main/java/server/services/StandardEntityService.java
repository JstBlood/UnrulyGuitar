package server.services;

import org.springframework.http.HttpStatus;

public interface StandardEntityService<T> {
    HttpStatus add(T cardList);
    HttpStatus update(long id, String component, String newValue);
    HttpStatus delete(long id);
}
