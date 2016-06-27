package sk.eea.arttag.game.model;

public class Card {

    private String token;
    private String source;

    public Card() {
    }

    public Card(String token, String source) {
        this.token = token;
        this.source = source;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

	@Override
	public String toString() {
		return "Card [token=" + token + ", source=" + source + "]";
	}
}
