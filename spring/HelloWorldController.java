package controller;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

// (1)
@Controller
public class HelloWorldController {
    // (2)
    @RequestMapping(value = "/", method = GET)
    public String home() {
        // (3)
        return "helloworld";
    }
}