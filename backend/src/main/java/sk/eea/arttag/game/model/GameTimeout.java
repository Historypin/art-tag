package sk.eea.arttag.game.model;

public class GameTimeout {

	private int timeoutGameCreated;
	private int timeoutRoundStarted;
	private int timeoutTopicSelected;
	private int timeoutOwnCardsSelected;
	private int timeoutRoundFinished;

	public GameTimeout(int timeoutGameCreated, int timeoutRoundStarted, int timeoutTopicSelected,
			int timeoutOwnCardsSelected, int timeoutRoundFinished) {
		this.timeoutGameCreated = timeoutGameCreated;
		this.timeoutRoundStarted = timeoutRoundStarted;
		this.timeoutTopicSelected = timeoutTopicSelected;
		this.timeoutOwnCardsSelected = timeoutOwnCardsSelected;
		this.timeoutRoundFinished = timeoutRoundFinished;
	}

	public int getTimeoutGameCreated() {
		return timeoutGameCreated;
	}
	public int getTimeoutRoundStarted() {
		return timeoutRoundStarted;
	}
	public int getTimeoutTopicSelected() {
		return timeoutTopicSelected;
	}
	public int getTimeoutOwnCardsSelected() {
		return timeoutOwnCardsSelected;
	}
	public int getTimeoutRoundFinished() {
		return timeoutRoundFinished;
	}
}
