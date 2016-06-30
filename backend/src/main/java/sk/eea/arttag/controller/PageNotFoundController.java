package sk.eea.arttag.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PageNotFoundController {

    @RequestMapping("/404")
    public String PageNotFound() {
        return "404";
    }
}
