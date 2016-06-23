package sk.eea.arttag.game.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sk.eea.arttag.game.model.Game;
import sk.eea.arttag.game.model.GameEvent;
import sk.eea.arttag.game.model.GameException;
import sk.eea.arttag.game.model.GamePlayerView;
import sk.eea.arttag.game.model.Player;
import sk.eea.arttag.game.model.RoundSummary;
import sk.eea.arttag.game.model.UserInput;
import sk.eea.arttag.game.model.UserInputType;

public class GameService {

	private static GameService instance;
	private static StateMachine stateMachine;

	private Map<String, Game> games = new HashMap<>();
	private static final Logger LOG = LoggerFactory.getLogger(GameService.class);
	public static final int ROUND_LENGTH_IN_SECONDS = 60;
	public static final int HAND_SIZE = 5;
	public static final int GAME_MIN_PLAYERS = 4;
	public static final int GAME_MAX_PLAYERS = 8;

	protected GameService() {
	}

	public static GameService getInstance() {
		if (instance == null) {
			instance = new GameService();
			instance.stateMachine = new StateMachine(instance);
			//TODO
			try {
				String uuid = UUID.randomUUID().toString();
				instance.create("1", uuid);
				uuid = UUID.randomUUID().toString();
				instance.create("2", uuid);
			} catch (GameException e) {
			}
		}
		return instance;
	}

	public Map<String, Game> getGames() {
		return games;
	}

	public List<GamePlayerView> getGameViews() throws GameException {

		List<GamePlayerView> views = new ArrayList<>();
		for (Game game : getGames().values()) {
			boolean gameTimeout = game.getRemainingTime() < 0;
			if (gameTimeout) {
				stateMachine.triggerEvent(game, GameEvent.TIMEOUT, null, null, null);
			}
			views.addAll(game.createGameViews());
		}
		return views;
	}

	public Game getGame(String token) {
    	LOG.debug("SERVICE:getGame() token: {}", token);
		return games.get(0);
	}

	//TODO:
	public void create(String id, String name) throws GameException {
		LOG.debug("CREATE");
		Game game = new Game(id, name, GAME_MIN_PLAYERS, GAME_MAX_PLAYERS, false);
		stateMachine.triggerEvent(game, GameEvent.GAME_CREATED, null, null, null);
	}

	//TODO:
	public void addPlayer(String userToken, String userId, String gameId) throws GameException {
		LOG.debug("ADD_PLAYER");
		Player player = new Player(userToken, userId, userId);
		Game game = getGames().get(gameId);
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
		Game game = this.games.get(gameId);
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
		GameEvent gameEvent = UserInputType.TAGS_SELECTED == input.getType() ? GameEvent.TAGS_SELECTED : (
				UserInputType.OWN_CARD_SELECTED == input.getType() ? GameEvent.PLAYER_OWN_CARD_SELECTED : (
						UserInputType.TABLE_CARD_SELECTED == input.getType() ? GameEvent.PLAYER_TABLE_CARD_SELECTED: (
								UserInputType.PLAYER_READY_FOR_NEXT_ROUND == input.getType() ? GameEvent.PLAYER_READY_FOR_NEXT_ROUND: (
										UserInputType.GAME_STARTED == input.getType() ? GameEvent.ROUND_STARTED: null
										)))
				);
		stateMachine.triggerEvent(game, gameEvent, input, userToken, null);
//		updateGameAfterUserInput(game, input, userToken);
	}

	public void processRoundSummary(RoundSummary summary) {
		
	}
}
