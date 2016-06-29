package sk.eea.arttag.game.model;

public class Card {

    private String token;
    private String source;
    private CardMetadata metadata;

    public Card() {
    }

    public Card(String token, String source, CardMetadata metadata) {
        this.token = token;
        this.source = source;
        this.metadata = metadata;
    }

    public String getToken() {
        return token;
    }

    public String getSource() {
        return source;
    }

    public CardMetadata getMetadata() {
        return metadata;
    }

    @Override
    public String toString() {
        return "Card [token=" + token + ", source=" + source + ", metadata=" + metadata + "]";
    }
}
