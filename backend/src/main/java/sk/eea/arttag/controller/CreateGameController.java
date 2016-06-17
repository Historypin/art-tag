package sk.eea.arttag.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CreateGameController {

    @RequestMapping("/create_game")
    public String createGame() {
        return "create_game";
    }
}
