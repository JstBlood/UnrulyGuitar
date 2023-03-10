package server;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/named")
public class SomeController {

    @GetMapping("/name/{name}")
    @ResponseBody
    public String named(@PathVariable("name") String name) {
        return "Hello " + name + "!";
    }
}