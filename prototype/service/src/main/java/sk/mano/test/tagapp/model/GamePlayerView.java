package sk.mano.test.tagapp.model;

import java.util.ArrayList;
import java.util.List;

public class GamePlayerView {

	private String userToken;
	private List<Card> hand = new ArrayList<>();
	private GameView gameView;

	public GamePlayerView() {
	}

	public String getUserToken() {
		return userToken;
	}
	public void setUserToken(String userToken) {
		this.userToken = userToken;
	}
	public List<Card> getHand() {
		return hand;
	}
	public void setHand(List<Card> hand) {
		this.hand = hand;
	}
	public GameView getGameView() {
		return gameView;
	}
	public void setGameView(GameView gameView) {
		this.gameView = gameView;
	}

	@Override
	public String toString() {
		return "GamePlayerView [userToken=" + userToken + ", hand=" + hand + ", gameView=" + gameView + "]";
	}
}
