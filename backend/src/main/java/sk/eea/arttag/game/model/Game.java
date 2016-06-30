package sk.eea.arttag.game.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Game {

    private static final Logger LOG = LoggerFactory.getLogger(Game.class);

    private String id;
    private String name;
    private Date created;
    private GameStatus status;
    //	private Map<String, Player> players = new HashMap<>();
    //	private Map<String, Player> players = Collections.synchronizedMap(new LinkedHashMap<>());
    private List<Player> players = new LinkedList<>();
    private Date endOfRound;
    private String tags;
    private List<Card> deck = new ArrayList<>();
    private List<Card> table = new ArrayList<>();
    private List<Card> tablePublic = new ArrayList<>();
    private int minPlayers;
    private int maxPlayers;
    private boolean privateGame = false;
    private String creatorUserId;
    private GameTimeout gameTimeout;

    public Game() {
    }

    public Game(String id, String name, int minPlayers, int maxPlayers, boolean privateGame, String creatorUserId, GameTimeout gameTimeout) {
        this.id = id;
        this.name = name;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        this.created = new Date();
        this.status = GameStatus.NEW;
        this.privateGame = privateGame;
        this.creatorUserId = creatorUserId;
        this.gameTimeout = gameTimeout;
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

    /*	public Map<String, Player> getPlayers() {
    		return players;
    	}
    	public void setPlayers(Map<String, Player> players) {
    		this.players = players;
    	}*/
    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public void addPlayer(Player player) {
        this.players.add(player);
    }

    /*	public void removePlayer(Player player) {
    		this.players.remove(player);
    	}*/

    public Date getEndOfRound() {
        return endOfRound;
    }

    public void setEndOfRound(Date endOfRound) {
        this.endOfRound = endOfRound;
    }

    public int getRemainingTime() {
        return (int) (endOfRound.getTime() - System.currentTimeMillis()) / 1000;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public List<Card> getDeck() {
        return deck;
    }

    public void setDeck(List<Card> deck) {
        this.deck = deck;
    }

    public List<Card> getTable() {
        return table;
    }
    public List<Card> getTablePublic() {
        return tablePublic;
    }

    /*	public List<Card> generateTableForPlayer(Player player) {
    	    List<Card> tableForPlayer = new ArrayList<>();
    	    getTable().forEach(c -> {
    	        Card clone = new Card(c);
    	        //mark my card
    	        if (player.getUserId().equals(c.getCardSelectedBy())) {
    	            clone.setCardSelectedBy(player.getUserId());
    	        }
    	        //if round has finished, mark dealers card
    	        if (player.isDealer() && GameStatus.ROUND_FINISHED == getStatus()) {
    	            clone.setDealersCard(true);
    	        }
    	        tableForPlayer.add(c);
    	    });
    	    return tableForPlayer;
    	}*/

    public void generateTable() {
        LOG.debug("Generate table for game {}, status {}", id, status);
        List<Card> table = new ArrayList<>();
        List<Card> tablePublic = new ArrayList<>();
        if (GameStatus.ROUND_OWN_CARDS_SELECTED == getStatus()) {
            table = getPlayers().stream().filter(p -> p.getOwnCardSelection() != null).map(p -> p.getOwnCardSelection())
                    .collect(Collectors.toList());
            tablePublic = table.stream().map(c -> new Card(c)).collect(Collectors.toList());
        } else if (GameStatus.ROUND_FINISHED == getStatus()) {
            tablePublic = getTable();
        }
        setTable(table);
        setTablePublic(tablePublic);
    }

    public void setTable(List<Card> table) {
        this.table = table;
    }
    public void setTablePublic(List<Card> tablePublic) {
        this.tablePublic = tablePublic;
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public boolean isPrivateGame() {
        return privateGame;
    }

    public String getCreatorUserId() {
        return creatorUserId;
    }

    public GameTimeout getGameTimeout() {
        return gameTimeout;
    }

    public void resetRound() {
        table = new ArrayList<>();
        tags = null;
    }

    public Player findPlayerByUserId(String userId) {
        /*		List<Player> players = getPlayers().values().stream()
        				.filter(p -> userId.equalsIgnoreCase(p.getUserId()))
        				.collect(Collectors.toList());*/
        List<Player> players = getPlayers().stream().filter(p -> userId.equalsIgnoreCase(p.getUserId())).collect(Collectors.toList());
        return players.size() > 0 ? players.get(0) : null;
    }

    public Player findPlayerByUserToken(String userToken) {
        List<Player> players = getPlayers().stream().filter(p -> userToken.equalsIgnoreCase(p.getToken())).collect(Collectors.toList());
        return players.size() > 0 ? players.get(0) : null;
    }

    @Override
    public String toString() {
        return "Game [getId()=" + getId() + ", getName()=" + getName() + ", getCreated()=" + getCreated() + ", getStatus()=" + getStatus() + ", getPlayers()="
                + getPlayers() + ", getEndOfRound()=" + getEndOfRound() + ", getRemainingTime()=" + getRemainingTime() + "]";
    }

    public List<GamePlayerView> createGameViews() {

        GameView view = new GameView();
        view.setRemainingTime(getRemainingTime());
        view.setTags(getTags());
        view.setCreated(getCreated());
        view.setEndOfRound(getEndOfRound());
        view.setId(getId());
        view.setName(getName());
        view.setPlayers(getPlayers());
        view.setStatus(getStatus());
        view.setTable(getTablePublic());

        List<GamePlayerView> gamePlayerViews = new ArrayList<>();
        //		for (Player player : players.values()) {
        for (Player player : players) {
            GamePlayerView gamePlayerView = new GamePlayerView();
            gamePlayerView.setGameView(view);
            gamePlayerView.setUserToken(player.getToken());
            gamePlayerView.setDealer(player.isDealer());
            gamePlayerView.setHand(player.getHand());
            gamePlayerViews.add(gamePlayerView);
            LOG.debug("GPV: {}", gamePlayerView.toString());
        }

        return gamePlayerViews;
    }

    public void updatePlayerScoreAfterRoundFinished(Map<String, Integer> summary) {
        getPlayers().forEach(p -> {
            int score = summary.getOrDefault(p.getUserId(), 0);
            p.setLastRoundScore(score);
            p.setGameScore(p.getGameScore() + score);
        });
    }
}
