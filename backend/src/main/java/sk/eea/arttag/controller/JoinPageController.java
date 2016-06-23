package sk.eea.arttag.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class JoinPageController {

    @RequestMapping("/join_page")
    public String join_page() {
        return "join_page";
    }
}
