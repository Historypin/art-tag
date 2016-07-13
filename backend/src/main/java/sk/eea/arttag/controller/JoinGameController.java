package sk.eea.arttag.controller;

import java.security.Principal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import sk.eea.arttag.game.model.GameException;
import sk.eea.arttag.game.model.GameException.GameExceptionType;
import sk.eea.arttag.game.service.GameService;

@Controller
public class JoinGameController {

    @Autowired
    GameService gameService;

    @RequestMapping(value = {"/join_game", "/join_game/{gameId}"})
    public String joinGame(@PathVariable Optional<String> gameIdOptional, Model model, Principal principal) {

        try {
            if (!gameIdOptional.isPresent()) {
                throw new GameException(GameExceptionType.GAME_NOT_FOUND);
            }
            String gameId = gameIdOptional.get();
            gameService.getGame(gameId);
            model.addAttribute("gameId", gameId);
            model.addAttribute("userId", principal.getName());
            return "join_game";

        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "404";
        }
    }
}
