package server;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class User {
    @Id
    String username;
    String password;
}
