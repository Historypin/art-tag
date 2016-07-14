package sk.eea.arttag.game.service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import sk.eea.arttag.ApplicationProperties;
import sk.eea.arttag.GameProperties;
import sk.eea.arttag.game.model.Card;
import sk.eea.arttag.game.model.Game;
import sk.eea.arttag.game.model.GameEvent;
import sk.eea.arttag.game.model.GameException;
import sk.eea.arttag.game.model.GameException.GameExceptionType;
import sk.eea.arttag.game.model.GamePlayerView;
import sk.eea.arttag.game.model.GameTimeout;
import sk.eea.arttag.game.model.Player;
import sk.eea.arttag.game.model.RoundSummary;
import sk.eea.arttag.game.model.UserInput;
import sk.eea.arttag.game.model.UserInputType;

@Component
public class GameService {

    private static final Logger LOG = LoggerFactory.getLogger(GameService.class);

    /**
     *  Collection containing all games.
     *  Key: game id.
     *  Value: game instance.
     */
    private static final Map<String, Game> GAMES = new ConcurrentHashMap<>();

    @Autowired
    private ApplicationProperties applicationProperties;

    @Autowired
    private GameProperties gameProperties;

    @Autowired
    private StateMachine stateMachine;

    @Autowired
    private CardService cardService;

    @Autowired
    private Environment environment;

    @PostConstruct
    public void init()
    {
        if(environment.acceptsProfiles("dev")) {
            try {
                this.create("lalahopapluha", "admin");
            } catch (GameException e) {
                LOG.error("Error at default game creation", e);
            }
        }
    }

    public List<GamePlayerView> getGameViews() {
        List<GamePlayerView> views = new ArrayList<>();
        for (Game game : getGames().values()) {
            boolean gameTimeout = game.getRemainingTime() < 0;
            if (gameTimeout) {
                try {
                    stateMachine.triggerEvent(game, GameEvent.TIMEOUT, null, null, null);
                } catch (GameException e) {
                    LOG.info("Game exception", e);
                    //TODO: display the message to a player/players
                }
            }
            views.addAll(game.createGameViews());
        }
        return views;
    }

    public Game create(String name, String creatorUserId) throws GameException {
        return this.create(name, creatorUserId, false);
    }

    public Game create(String name, String creatorUserId, boolean privateGame) throws GameException {
        final GameTimeout gameTimeout = new GameTimeout(
                gameProperties.getTimeoutGameCreated(),
                gameProperties.getTimeoutRoundStarted(),
                gameProperties.getTimeoutTopicSelected(),
                gameProperties.getTimeoutOwnCardsSelected(),
                gameProperties.getTimeoutRoundFinished()
        );

        if(!privateGame) { // watch for duplicates in non-private games
            boolean nameIsAlreadyInUse = GAMES.values().parallelStream().anyMatch(game -> name.equals(game.getName()));
            if(nameIsAlreadyInUse) {
                throw new GameException(GameException.GameExceptionType.GAME_NAME_ALREADY_IN_USE);
            }
        }

        // probability of UUID collision seems implausible, so lets hope for the best
        final UUID uuid = UUID.randomUUID();

        Game game = new Game(uuid.toString(), name, gameProperties.getMinimumGamePlayers(), gameProperties.getMaximumGamePlayers(), privateGame, creatorUserId, gameTimeout);
        GAMES.put(uuid.toString(), game);

        stateMachine.triggerEvent(game, GameEvent.GAME_CREATED, null, null, null);

        LOG.debug("Created new game: {}", game);
        return game;
    }

    public Game getGame(String gameId) throws GameException {
        Game game = getGames().get(gameId);
        if (game == null) {
            throw new GameException(GameExceptionType.GAME_NOT_FOUND);
        }
        return game;
    }

    //TODO:
    public void addPlayer(String userToken, String userId, String gameId) throws GameException {
        Game game = getGame(gameId);
        LOG.debug("ADD_PLAYER");
        Player player = new Player(userToken, userId, userId);
        stateMachine.triggerEvent(game, GameEvent.PLAYER_JOINED, null, userToken, player);
    }

    public void removePlayer(String userToken) {
        LOG.debug("REMOVE_PLAYER");
        for (Game game : getGames().values()) {
            //			Player player = game.getPlayers().get(token);
            Player player = game.findPlayerByUserToken(userToken);
            if (player != null) {
                player.setInactive(true);
            }
            //TODO: trigger event
        }
    }

    public void userInput(String userToken, UserInput input) throws GameException {
        String gameId = input.getGameId();
        if (gameId == null || input == null || input.getType() == null || input.getValue() == null) {
            //ignore
            LOG.debug("one of (gameId, input, input.type, input.value) null");
            return;
        }
        Game game = GAMES.get(gameId);
        if (game == null) {
            //ignore
            LOG.debug("Game null");
            return;
        }

        //check userToken in current game
        //		Player player = game.getPlayers().get(userToken);
        Player player = game.findPlayerByUserToken(userToken);
        if (player == null) {
            //ignore
            LOG.debug("Player null");
            return;
        }

        //evaluate possible events a user can trigger
        GameEvent gameEvent = UserInputType.TOPIC_SELECTED == input.getType() ? GameEvent.TAGS_SELECTED
                : (UserInputType.OWN_CARD_SELECTED == input.getType() ? GameEvent.PLAYER_OWN_CARD_SELECTED
                : (UserInputType.TABLE_CARD_SELECTED == input.getType() ? GameEvent.PLAYER_TABLE_CARD_SELECTED
                : (UserInputType.PLAYER_READY_FOR_NEXT_ROUND == input.getType() ? GameEvent.PLAYER_READY_FOR_NEXT_ROUND
                : (UserInputType.GAME_STARTED == input.getType() ? GameEvent.ROUND_STARTED : null))));
        stateMachine.triggerEvent(game, gameEvent, input, userToken, null);
        //		updateGameAfterUserInput(game, input, userToken);
    }

    public void processRoundSummary(RoundSummary summary) {
        cardService.save(summary.getCardSummary(), summary.getGame().getTags(), CardService.CARD_DESCRIPTION_DEFAULT_LANGUAGE);
    }

    public List<Card> getInitialDeck(int numberOfCards) {
        LOG.debug("INITIAL_DECK");
        return cardService.getCards(numberOfCards);
    }

    public Map<String, Game> getGames() {
        return GAMES;
    }
}
