package sk.eea.arttag.game.model;

import java.util.ArrayList;
import java.util.List;

public class Player {

	private String token;
	private String name;
	private List<Card> hand = new ArrayList<>();
	private Card ownBid;
	private Card matchingCardSelected;
	private boolean dealer;

	public Player() {
	}
	public Player(String token, String name) {
		this.token = token;
		this.name = name;
	}

	public List<Card> getHand() {
		return hand;
	}
	public void setHand(List<Card> hand) {
		this.hand = hand;
	}
	public Card getOwnBid() {
		return ownBid;
	}
	public void setOwnBid(Card ownBid) {
		this.ownBid = ownBid;
	}
	public Card getMatchingCardSelected() {
		return matchingCardSelected;
	}
	public void setMatchingCardSelected(Card matchingCardSelected) {
		this.matchingCardSelected = matchingCardSelected;
	}
	public boolean isDealer() {
		return dealer;
	}
	public void setDealer(boolean dealer) {
		this.dealer = dealer;
	}

	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Player [token=" + token + ", name=" + name + ", hand=" + hand + ", ownBid=" + ownBid
				+ ", matchingCardSelected=" + matchingCardSelected + ", dealer=" + dealer + "]";
	}

}
