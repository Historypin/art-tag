package sk.eea.arttag.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
public class JoinGameController {

    @RequestMapping("/join_game/{gameId}")
    public String joinGame(@PathVariable String gameId, Model model, Principal principal) {
    	model.addAttribute("gameId", gameId);
        model.addAttribute("userId", principal.getName());
        return "join_game";
    }
}
