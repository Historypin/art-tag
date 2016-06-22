package sk.eea.arttag.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class JoinGameController {

    @RequestMapping("/join_game/{gameId}")
    public String joinGame(@PathVariable String gameId, Model model) {
    	model.addAttribute("gameId", gameId);
        return "join_game";
    }
}
