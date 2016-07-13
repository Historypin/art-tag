package sk.eea.arttag.game.model;

public class CardRoundSummary {

    private Long culturalObjectId;
    private int score;

    public CardRoundSummary(Long culturalObjectId, int score) {
        this.culturalObjectId = culturalObjectId;
        this.score = score;
    }

    public Long getCulturalObjectId() {
        return culturalObjectId;
    }
    public int getScore() {
        return score;
    }
}
