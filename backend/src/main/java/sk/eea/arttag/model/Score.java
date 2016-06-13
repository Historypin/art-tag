package sk.eea.arttag.model;

/**
 * Contains score of a single player
 * @author Maros Strmensky
 *
 */
public class Score {
	
	private Long gamesPlayed;
	private Long gamesWon;
	private Long totalScore;
	
	public Long getGamesPlayed() {
		return gamesPlayed;
	}
	public void setGamesPlayed(Long gamesPlayed) {
		this.gamesPlayed = gamesPlayed;
	}
	public Long getGamesWon() {
		return gamesWon;
	}
	public void setGamesWon(Long gamesWon) {
		this.gamesWon = gamesWon;
	}
	public Long getTotalScore() {
		return totalScore;
	}
	public void setTotalScore(Long totalScore) {
		this.totalScore = totalScore;
	}
}
