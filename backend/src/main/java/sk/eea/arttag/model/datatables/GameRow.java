package sk.eea.arttag.model.datatables;

public class GameRow {

    private String gameId;

    private String name;

    private Integer numberOfPlayers;

    public GameRow() {
    }

    public GameRow(String gameId, String name, Integer numberOfPlayers) {
        this.gameId = gameId;
        this.name = name;
        this.numberOfPlayers = numberOfPlayers;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getNumberOfPlayers() {
        return numberOfPlayers;
    }

    public void setNumberOfPlayers(Integer numberOfPlayers) {
        this.numberOfPlayers = numberOfPlayers;
    }

    @Override
    public String toString() {
        return "GameRow{" +
                "gameId='" + gameId + '\'' +
                ", name='" + name + '\'' +
                ", numberOfPlayers=" + numberOfPlayers +
                '}';
    }
}
