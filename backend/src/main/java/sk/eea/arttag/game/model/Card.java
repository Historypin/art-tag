package sk.eea.arttag.game.model;

import java.util.ArrayList;
import java.util.List;

public class Card {

    private String token;
    private String source;
    private CardMetadata metadata;
    private List<String> playerSelections = new ArrayList<>();

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
    public void addPlayerSelection(String userId) {
        playerSelections.add(userId);
    }
    public List<String> getPlayerSelections() {
        return playerSelections;
    }

    @Override
    public String toString() {
        return "Card [token=" + token + ", source=" + source + ", metadata=" + metadata + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((token == null) ? 0 : token.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Card other = (Card) obj;
        if (token == null) {
            if (other.token != null)
                return false;
        } else if (!token.equals(other.token))
            return false;
        return true;
    }

}
