package sk.eea.arttag.game.model;

import java.util.ArrayList;
import java.util.List;

public class Card {

    private String token;
    private Long culturalObjectId;
    private String source;
    private CardMetadata metadata;
    private List<String> playerSelections = new ArrayList<>();
    private String cardSelectedBy; //userId of the player who selected this card
    private boolean dealersCard;

    public Card() {
    }

    public Card(String token, Long culturalObjectId, String source, CardMetadata metadata) {
        this.token = token;
        this.culturalObjectId = culturalObjectId;
        this.source = source;
        this.metadata = metadata;
    }

    public Card(Card original, String userId) {
        this.token = original.getToken();
        this.culturalObjectId = original.getCulturalObjectId();
        this.source = original.getSource();
        this.metadata = original.getMetadata();
        this.playerSelections = new ArrayList<>();
        this.cardSelectedBy = userId.equalsIgnoreCase(original.getCardSelectedBy()) ? userId : null;
        this.dealersCard = false;
    }

    public String getToken() {
        return token;
    }
    public Long getCulturalObjectId() {
        return culturalObjectId;
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
    public boolean isDealersCard() {
        return dealersCard;
    }
    public void setDealersCard(boolean dealersCard) {
        this.dealersCard = dealersCard;
    }
    public String getCardSelectedBy() {
        return cardSelectedBy;
    }
    public void setCardSelectedBy(String cardSelectedBy) {
        this.cardSelectedBy = cardSelectedBy;
    }

    @Override
    public String toString() {
        return "Card [token=" + token + ", culturalObjectId=" + culturalObjectId + ", source=" + source + ", playerSelections=" + playerSelections
                + ", cardSelectedBy=" + cardSelectedBy + ", dealersCard=" + dealersCard + "]";
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
