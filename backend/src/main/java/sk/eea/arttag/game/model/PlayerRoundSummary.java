package sk.eea.arttag.game.model;

public class PlayerRoundSummary {

	private String userId;
	private int score;

	public PlayerRoundSummary(String userId, int score) {
		this.userId = userId;
		this.score = score;
	}

	public String getUserId() {
		return userId;
	}

	public int getScore() {
		return score;
	}
}
