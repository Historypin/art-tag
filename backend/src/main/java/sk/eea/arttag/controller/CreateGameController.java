package sk.eea.arttag.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import sk.eea.arttag.ApplicationProperties;
import sk.eea.arttag.game.model.Game;
import sk.eea.arttag.game.model.GameException;
import sk.eea.arttag.game.service.GameService;
import sk.eea.arttag.model.form.CreateGameForm;

import javax.validation.Valid;
import java.security.Principal;

@Controller
public class CreateGameController {

    private static final Logger LOG = LoggerFactory.getLogger(CreateGameController.class);

    @Autowired
    private GameService gameService;

    @Autowired
    private ApplicationProperties applicationProperties;

    @RequestMapping(value = "/create_game", method = RequestMethod.GET)
    public String get(CreateGameForm createGameForm) {
        return "create_game";
    }

    @RequestMapping(value = "/create_game", method = RequestMethod.POST)
    public String post(@Valid CreateGameForm createGameForm, BindingResult bindingResult, Principal principal) {
        if (bindingResult.hasErrors()) {
            return "create_game";
        }

        Game game;
        try {
            game = gameService.create(createGameForm.getName(), principal.getName(), createGameForm.isPrivateGame());
        } catch (GameException e) {
            bindingResult.addError(new FieldError("createGameForm", "name", "Game with this name already exists. Please choose different."));
            return "create_game";
        }

        if (!createGameForm.isPrivateGame()) {
            return String.format("redirect:join_game/%s", game.getId());
        }

        final String joinGameLink = String.format("%s://%s/join_game/%s", applicationProperties.getHostnamePrefix(), applicationProperties.getHostname(), game.getId());
        createGameForm.setJoinGameLink(joinGameLink);
        return "create_game";
    }

}
