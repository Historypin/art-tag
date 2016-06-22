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
import sk.eea.arttag.game.model.GamePlayerView;
import sk.eea.arttag.game.model.Player;
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
			String uuid = UUID.randomUUID().toString();
			instance.create("1", uuid);
			uuid = UUID.randomUUID().toString();
			instance.create("2", uuid);
		}
		return instance;
	}

	public Map<String, Game> getGames() {
		return games;
	}

	public List<GamePlayerView> getGameViews() {

		List<GamePlayerView> views = new ArrayList<>();
		for (Game game : getGames().values()) {
			boolean gameTimeout = game.getRemainingTime() < 0;
			if (gameTimeout) {
				stateMachine.triggerEvent(game, GameEvent.TIMEOUT, null, null);
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
	public void create(String id, String name) {
		LOG.debug("CREATE");
		Game game = new Game(id, name, GAME_MIN_PLAYERS, GAME_MAX_PLAYERS, false);
		stateMachine.triggerEvent(game, GameEvent.GAME_CREATED, null, null);
	}

	//TODO:
	public void addPlayer(String userToken, String userId, String gameId) {
		LOG.debug("ADD_PLAYER");
		Player player = new Player(userToken, userId, userId);
		Game game = getGames().get(gameId);
		//TODO: evaluate game not null, game status, number of active players, private/public, join/rejoin
		game.addPlayer(player);
		stateMachine.triggerEvent(game, GameEvent.PLAYER_JOINED, null, userToken);
	}

	public void removePlayer(String token) {
		LOG.debug("REMOVE_PLAYER");
		for (Game game : getGames().values()) {
			game.getPlayers().remove(token);
		}
	}

	public void userInput(String userToken, UserInput input) {
		String gameId = input.getGameId();
		if (gameId == null || input == null || input.getType() == null || input.getValue() == null) {
			//ignore
			return;
		}
		Game game = this.games.get(gameId);
		if (game == null) {
			//ignore
			return;
		}

		//check userToken in current game
		Player player = game.getPlayers().get(userToken);
		if (player == null) {
			//ignore
			return;
		}

		//evaluate possible events a user can trigger
		GameEvent gameEvent = UserInputType.TAGS_SELECTED == input.getType() ? GameEvent.TAGS_SELECTED : (
				UserInputType.OWN_CARD_SELECTED == input.getType() ? GameEvent.PLAYER_OWN_CARD_SELECTED : (
						UserInputType.TABLE_CARD_SELECTED == input.getType() ? GameEvent.PLAYER_TABLE_CARD_SELECTED: (
								UserInputType.PLAYER_READY_FOR_NEXT_ROUND == input.getType() ? GameEvent.PLAYER_READY_FOR_NEXT_ROUND: null))
				);
		stateMachine.triggerEvent(game, gameEvent, input, userToken);
//		updateGameAfterUserInput(game, input, userToken);
	}

/*	private void updateGame(Game game, GameEvent reason) {
		LOG.debug("UPDATE_GAME {}", reason.name());

		switch (reason) {
		case GAME_CREATED:
			game.setStatus(GameStatus.CREATED);
			game.setEndOfRound(timeout(ROUND_LENGTH_IN_SECONDS));//FIXME: remove
			this.games.put(game.getId(), game);
			break;

//		case PLAYER_JOINED:
//		case PLAYER_DISCONNECTED:
		case ROUND_STARTED:
			game.setStatus(GameStatus.ROUND_STARTED);
			game.setEndOfRound(timeout(ROUND_LENGTH_IN_SECONDS));
			break;

		case TAGS_SELECTED:
			game.setStatus(GameStatus.ROUND_TAGS_SELECTED);
			game.setEndOfRound(timeout(ROUND_LENGTH_IN_SECONDS));
			break;

		case PLAYER_OWN_CARD_SELECTED: {
			//check if all players already selected a table card
			boolean notYetAllPlayersSelected = game.getPlayers().values().stream().anyMatch(p -> p.getOwnCardSelection() == null);
			if (!notYetAllPlayersSelected) {
				game.setStatus(GameStatus.ROUND_OWN_CARDS_SELECTED);
				game.setEndOfRound(timeout(ROUND_LENGTH_IN_SECONDS));
			}
			break;
		}

		case ALL_PLAYERS_OWN_CARD_SELECTED:
			game.setStatus(GameStatus.ROUND_OWN_CARDS_SELECTED);
			game.setEndOfRound(timeout(ROUND_LENGTH_IN_SECONDS));
			break;

		case ALL_PLAYERS_TABLE_CARD_SELECTED:
			game.setStatus(GameStatus.ROUND_TABLE_CARDS_SELECTED);
			game.setEndOfRound(timeout(ROUND_LENGTH_IN_SECONDS));
			break;

		case PLAYER_TABLE_CARD_SELECTED: {
			//check if all players already selected a table card
			boolean notYetAllPlayersSelected = game.getPlayers().values().stream().anyMatch(p -> p.getTableCardSelection() == null);
			if (!notYetAllPlayersSelected) {
				game.setStatus(GameStatus.ROUND_TABLE_CARDS_SELECTED);
				game.setEndOfRound(timeout(ROUND_LENGTH_IN_SECONDS));
			}
			break;
		}

		case TIMEOUT:
			switch (game.getStatus()) {
			case CREATED:
				start(game.getId());
				break;

			case ROUND_STARTED:
				startRound(game.getId());
				break;

			case ROUND_TAGS_SELECTED:
				updateGame(game, GameEvent.ALL_PLAYERS_OWN_CARD_SELECTED);
				break;

			case ROUND_OWN_CARDS_SELECTED:
				updateGame(game, GameEvent.ALL_PLAYERS_TABLE_CARD_SELECTED);
				break;

			case ROUND_TABLE_CARDS_SELECTED:
//				updateGame(game, GameUpdateReason.);
				break;

			case ROUND_FINISHED:
				startRound(game.getId());
				break;

			default:
				break;
			}
			break;

		default:
			break;
		}
	}*/

/*	private void updateGameAfterUserInput(Game game, UserInput input, String userToken) {

		//check userToken in current game
		Player player = game.getPlayers().get(userToken);
		if (player == null) {
			//ignore
			return;
		}

		//TODO check possible states for user input to be valid

		switch (input.getType()) {
		case TAGS_SELECTED:
			game.setTags(input.getValue());
			updateGame(game, GameEvent.TAGS_SELECTED);
			break;

		case OWN_CARD_SELECTED: {
			String cardToken = input.getValue();
			//find the card by token
			Card card = player.getHand().stream().filter(c -> cardToken.equalsIgnoreCase(c.getToken())).findFirst().get();
			if (card == null) {
				//ignore
				return;
			}
			//pop the card out of players hand
			player.getHand().remove(card);
			//set to ownSelection
			player.setOwnCardSelection(card);
			updateGame(game, GameEvent.PLAYER_OWN_CARD_SELECTED);
			break;
		}

		case TABLE_CARD_SELECTED: {
			String cardToken = input.getValue();
			//find the card by token
			Card card = player.getHand().stream().filter(c -> cardToken.equalsIgnoreCase(c.getToken())).findFirst().get();
			if (card == null) {
				//ignore
				return;
			}
			//pop the card out of players hand
			player.getHand().remove(card);
			//set to tableSelection
			player.setTableCardSelection(card);
			updateGame(game, GameEvent.PLAYER_TABLE_CARD_SELECTED);
			break;
		}

		default:
			break;
		}
	}*/

}
