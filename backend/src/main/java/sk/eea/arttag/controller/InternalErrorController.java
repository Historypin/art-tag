package sk.eea.arttag.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class InternalErrorController {

    @RequestMapping("/500")
    public String InternalError() {
        return "500";
    }
}
