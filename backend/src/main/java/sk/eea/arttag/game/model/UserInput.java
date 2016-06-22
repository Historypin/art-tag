package sk.eea.arttag.game.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class UserInput {

	private String value;

	private UserInputType type;

	private String gameId;

    private String playerName;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public UserInputType getType() {
        return type;
    }

    public void setType(UserInputType type) {
        this.type = type;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    @Override
    public String toString() {
        return "UserInput{" +
            "value='" + value + '\'' +
            ", type=" + type +
            ", gameId='" + gameId + '\'' +
            ", playerName='" + playerName + '\'' +
            '}';
    }
}
