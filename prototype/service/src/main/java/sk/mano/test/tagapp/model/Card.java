package sk.mano.test.tagapp.model;

import java.util.Random;

public class Card {

	private String token;

	public Card() {
		Random r = new Random();
		char c = (char) (r.nextInt(26) + 'A');
		this.token = String.valueOf(c);
	}

	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}

	@Override
	public String toString() {
		return token;
	}
}
