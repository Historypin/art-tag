package sk.eea.arttag.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IntroController {

    @RequestMapping("/intro")
    public String intro() {
        return "intro";
    }
}
