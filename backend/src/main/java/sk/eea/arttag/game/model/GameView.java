package sk.eea.arttag.game.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class GameView {

	private String id;
	private String name;
    @JsonIgnore
	private Date created;
	private GameStatus status;
    @JsonIgnore
    private Date endOfRound;
	private String tags;
	private int remainingTime;
	private List<Player> players = new LinkedList<>();
	private List<Card> table = new ArrayList<>();

	public GameView() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
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

	public Date getEndOfRound() {
		return endOfRound;
	}

	public void setEndOfRound(Date endOfRound) {
		this.endOfRound = endOfRound;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public int getRemainingTime() {
		return remainingTime;
	}

	public void setRemainingTime(int remainingTime) {
		this.remainingTime = remainingTime;
	}

	public List<Player> getPlayers() {
		return players;
	}
	public void setPlayers(List<Player> players) {
		this.players = players;
	}

	public List<Card> getTable() {
		return table;
	}

	public void setTable(List<Card> table) {
		this.table = table;
	}

	@Override
	public String toString() {
		return "GameView [id=" + id + ", name=" + name + ", created=" + created + ", status=" + status + ", endOfRound="
				+ endOfRound + ", tags=" + tags + ", remainingTime=" + remainingTime + ", players=" + players
				+ ", table=" + table + "]";
	}

}
