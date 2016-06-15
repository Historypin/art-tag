package sk.eea.arttag.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class JoinGameController {

    @RequestMapping("/join_game")
    public String joinGame() {
        return "join_game";
    }
}
