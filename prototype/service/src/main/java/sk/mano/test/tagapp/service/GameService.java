package sk.mano.test.tagapp.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sk.mano.test.tagapp.model.Card;
import sk.mano.test.tagapp.model.Game;
import sk.mano.test.tagapp.model.GamePlayerView;
import sk.mano.test.tagapp.model.GameView;
import sk.mano.test.tagapp.model.Player;

public class GameService {

	private static GameService instance;

	private List<Game> games = GAMES;
	private static final Logger LOG = LoggerFactory.getLogger(GameService.class);

	protected GameService() {
	}

	public static GameService getInstance() {
		if (instance == null) {
			instance = new GameService();
		}
		return instance;
	}

	public List<Game> getGames() {
		return GAMES;
	}

	public List<GamePlayerView> getGameViews() {
		List<GamePlayerView> views = new ArrayList<>();
		for (Game game : getGames()) {
			views.addAll(game.createGameViews());
		}
		return views;
	}

	public Game getGame(String token) {
    	LOG.debug("SERVICE:getGame() token: {}", token);
		return games.get(0);
	}

	//TODO:
	public Game create() {
		return null;
	}

	//TODO:
	public void addPlayer(String token) {
		Player player = new Player(token, UUID.randomUUID().toString());
		player.setHand(randomCards());
		int i = Integer.valueOf(token);
		int rem = i % 2;
		getGames().get(rem).addPlayer(player);
	}

	public void removePlayer(String token) {
		for (Game game : getGames()) {
			game.getPlayers().removeIf(pl -> token.equalsIgnoreCase(pl.getToken()));
		}
	}

	private static final List<Game> GAMES = new ArrayList(){{

		Game g1 = new Game();
		g1.setId(1L);
		g1.setName("game 1");
		g1.setCreated(new Date());

		Game g2 = new Game();
		g2.setId(2L);
		g2.setName("game 2");
		g2.setCreated(new Date());

		add(g1);
		add(g2);
	}};

	private List<Card> randomCards() {
		return new ArrayList() {{
			add(new Card());
			add(new Card());
			add(new Card());
			add(new Card());
			add(new Card());
		}};
	}
}
