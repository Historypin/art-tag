package sk.eea.arttag.controller;

import java.security.Principal;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import sk.eea.arttag.game.model.GameException;
import sk.eea.arttag.game.model.GameException.GameExceptionType;
import sk.eea.arttag.game.service.GameService;

@Controller
public class JoinGameController {

    @Autowired
    GameService gameService;

    private static final Logger LOG = LoggerFactory.getLogger(JoinGameController.class);

    @RequestMapping(method = RequestMethod.GET, value = {"/join_game/{gameId}", "/join_game"})
//    public String joinGame(@PathVariable Optional<String> gameIdOptional, Model model, Principal principal) {
    public String joinGame(@PathVariable String gameId, Model model, Principal principal) {

        try {
/*            if (!gameIdOptional.isPresent()) {
                LOG.debug("gameId not present in URL");
                throw new GameException(GameExceptionType.GAME_NOT_FOUND);
            }
            String gameId = gameIdOptional.get();*/
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
