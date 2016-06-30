package sk.eea.arttag.game.model;

public class CardRoundSummary {

    private String cardToken;
    private int score;

    public CardRoundSummary(String cardToken, int score) {
        this.cardToken = cardToken;
        this.score = score;
    }

    public String getCardToken() {
        return cardToken;
    }

    public int getScore() {
        return score;
    }
}
