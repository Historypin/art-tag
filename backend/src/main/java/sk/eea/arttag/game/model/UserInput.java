package sk.eea.arttag.game.model;

public class UserInput {

	private String value;
	private UserInputType type;
	private String gameId;

	public UserInput() {
	}

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
}
