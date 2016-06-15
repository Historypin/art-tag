package sk.eea.arttag.game.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Game {

	private Long id;
	private String name;
	private Date created;
	private GameStatus status;
	private List<Player> players = new ArrayList<>();
	private Date endOfRound;
	private String tags;

	public Game() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public GameStatus getStatus() {
		return status;
	}

	public void setStatus(GameStatus status) {
		this.status = status;
	}

	public List<Player> getPlayers() {
		return players;
	}

	public void setPlayers(List<Player> players) {
		this.players = players;
	}
	public void addPlayer(Player player) {
		this.players.add(player);
	}
	public void removePlayer(Player player) {
		this.players.remove(player);
	}

	public Date getEndOfRound() {
		return endOfRound;
	}

	public void setEndOfRound(Date endOfRound) {
		this.endOfRound = endOfRound;
	}	

	public int getRemainingTime() {
		return (int)(System.currentTimeMillis() - created.getTime()) / 1000;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}


	@Override
	public String toString() {
		return "Game [getId()=" + getId() + ", getName()=" + getName() + ", getCreated()=" + getCreated()
				+ ", getStatus()=" + getStatus() + ", getPlayers()=" + getPlayers() + ", getEndOfRound()="
				+ getEndOfRound() + ", getRemainingTime()=" + getRemainingTime() + "]";
	}

	public List<GamePlayerView> createGameViews() {

		GameView view = new GameView();
		view.setRemainingTime(getRemainingTime());
		view.setTable(null);//TODO
		view.setTags(getTags());
		view.setCreated(getCreated());
		view.setEndOfRound(getEndOfRound());
		view.setId(getId());
		view.setName(getName());
		view.setPlayers(getPlayers());
		view.setStatus(getStatus());

		List<GamePlayerView> gamePlayerViews = new ArrayList<>();
		for (Player player : players) {
			GamePlayerView gamePlayerView = new GamePlayerView();
			gamePlayerView.setGameView(view);
			gamePlayerView.setUserToken(player.getToken());
			gamePlayerView.setHand(player.getHand());
			gamePlayerViews.add(gamePlayerView);
		}

		return gamePlayerViews;
	}
}
