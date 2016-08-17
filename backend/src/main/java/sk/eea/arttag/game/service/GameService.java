package sk.eea.arttag.game.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import sk.eea.arttag.ApplicationProperties;
import sk.eea.arttag.GameProperties;
import sk.eea.arttag.controller.WebSocketGameController;
import sk.eea.arttag.game.model.Card;
import sk.eea.arttag.game.model.Game;
import sk.eea.arttag.game.model.GameEvent;
import sk.eea.arttag.game.model.GameException;
import sk.eea.arttag.game.model.GameException.GameExceptionType;
import sk.eea.arttag.game.model.GamePlayerView;
import sk.eea.arttag.game.model.GameStatus;
import sk.eea.arttag.game.model.GameTimeout;
import sk.eea.arttag.game.model.Player;
import sk.eea.arttag.game.model.RoundSummary;
import sk.eea.arttag.game.model.UserInput;
import sk.eea.arttag.game.model.UserInputType;
import sk.eea.arttag.model.User;
import sk.eea.arttag.repository.UserRepository;

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
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WebSocketGameController webSocketController;

    @PostConstruct
    public void init()
    {
        if(gameProperties.getCreateDefaultGame()) {
            try {
                LOG.debug("Creating default game");
                User user = userRepository.findByEmail("admin@email.sk");
                this.create("lalahopapluha", user.getId());
                for (int i = 0; i < 100; i++) {
                    create("game"+i, user.getId(), false, false);
                }
            } catch (GameException e) {
                LOG.error("Error at default game creation", e);
            }
        }
    }

    public void gameUpdated(Game game) {
        
        List<GamePlayerView> views = game.createGameViews();
        webSocketController.trigger(views);
        if (GameStatus.FINISHED == game.getStatus()) {
            List<String> userTokens = game.getPlayers().stream().map(p -> p.getToken()).collect(Collectors.toList());
            webSocketController.triggerClose(userTokens);
            getGames().remove(game.getId());
        }
    }

    public void triggerGameTimeout(Game game) {
        try {
            stateMachine.triggerEvent(game, GameEvent.TIMEOUT, null, null, null);
        } catch (GameException e) {
            LOG.info("Game exception, game: {}, reason: {}", game.getId(), e.getMessage());
            //TODO: display the message to a player/players
        }
    }

    public Game create(String name, Long creatorUserId) throws GameException {
        return this.create(name, creatorUserId, false, true);
    }

    public Game create(String name, Long creatorUserId, boolean privateGame, boolean uuidGeneratedGameId) throws GameException {
        final GameTimeout gameTimeout = new GameTimeout(gameProperties.getTimeoutGameCreated(), gameProperties.getTimeoutRoundStarted(),
                gameProperties.getTimeoutTopicSelected(), gameProperties.getTimeoutOwnCardsSelected(), gameProperties.getTimeoutRoundFinished());

        if (!privateGame) { // watch for duplicates in non-private games
            boolean nameIsAlreadyInUse = GAMES.values().parallelStream().anyMatch(game -> name.equals(game.getName()));
            if (nameIsAlreadyInUse) {
                throw new GameException(GameException.GameExceptionType.GAME_NAME_ALREADY_IN_USE);
            }
        }

        // probability of UUID collision seems implausible, so lets hope for the best
        String gameId = null;
        if (uuidGeneratedGameId) {
            gameId = UUID.randomUUID().toString();
        } else {
            gameId = name;
        }

        Game game = new Game(gameId, name, gameProperties.getMinimumGamePlayers(), gameProperties.getMaximumGamePlayers(), privateGame, creatorUserId,
                gameTimeout);
        GAMES.put(gameId, game);

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
    public void addPlayer(String userToken, Long userId, String gameId) throws GameException {
        Game game = getGame(gameId);
        User user = userRepository.findOne(userId);
        LOG.debug("ADD_PLAYER, userId: {}, gameId: {}", userId, gameId);
        Player player = new Player(userToken, user.getNickName(), userId);
        stateMachine.triggerEvent(game, GameEvent.PLAYER_JOINED, null, userToken, player);
    }

    public void removePlayer(String userToken) throws GameException {
        LOG.debug("REMOVE_PLAYER");
        for (Game game : getGames().values()) {
            //			Player player = game.getPlayers().get(token);
            Player player = game.findPlayerByUserToken(userToken);
            if (player != null) {
                LOG.debug("REMOVE_PLAYER, setInactive: {}", player.getUserId());
                player.setInactive(true);
                stateMachine.triggerEvent(game, GameEvent.PLAYER_DISCONNECTED, null, userToken, player);
            }
        }
    }

    public void userInput(String userToken, UserInput input) throws GameException {
        LOG.debug("UserInput, user: {}, input: {}", userToken, input);
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

        LOG.debug("Triggering processing of user input");
        stateMachine.triggerEvent(game, gameEvent, input, userToken, null);
        //		updateGameAfterUserInput(game, input, userToken);
    }

    public void processGameSummary(Game game) {

        //calculate winner, update players with gamesPlayed, gamesWon
        cardService.updatePlayersAfterGameFinished(game);
    }

    public void processRoundSummary(RoundSummary summary) {
        if (summary == null) {
            return;
        }
        //update tags
        cardService.saveTags(summary.getCardSummary(), summary.getGame().getTags(), summary.getPlayerSummary().size(), CardService.CARD_DESCRIPTION_DEFAULT_LANGUAGE);
        //update players with round score, 
        cardService.updatePlayersAfterRoundFinished(summary.getPlayerSummary());
    }

    public List<Card> getInitialDeck(int numberOfCards, Game game) {
        LOG.debug("INITIAL_DECK");
        //try to fetch cards, avoid the cards already contained in players hands
        List<Long> culturalObjectIds = game.getPlayers().stream().flatMap(p -> p.getHand().stream()).map(c -> c.getCulturalObjectId()).collect(Collectors.toList());
        return cardService.getCards(numberOfCards, "en", culturalObjectIds);
    }

    public Map<String, Game> getGames() {
        return GAMES;
    }
}
