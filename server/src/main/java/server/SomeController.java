package server;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/")
public class SomeController {
    private UserRepo repo;

    public SomeController(UserRepo r) {
        repo = r;
    }

    @GetMapping("/")
    @ResponseBody
    public String index() {
        return repo.existsById("dupa") ? "nogame" : "game";
    }

    @GetMapping("/gowno")
    @ResponseBody
    public String inded() {
        User u = new User();
        u.password = "asdasd";
        u.username = "dupa";
        repo.save(u);
        return "gowno";
    }
}