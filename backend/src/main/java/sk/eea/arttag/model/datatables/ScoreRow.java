package sk.eea.arttag.model.datatables;

public class ScoreRow {

    private String userId;
    private String userName;
    private Long gamesPlayed;
    private Long gamesWon;
    private Long totalScore;

    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public Long getGamesPlayed() {
        return gamesPlayed == null ? 0L : gamesPlayed;
    }
    public void setGamesPlayed(Long gamesPlayed) {
        this.gamesPlayed = gamesPlayed;
    }
    public Long getGamesWon() {
        return gamesWon == null ? 0L : gamesWon;
    }
    public void setGamesWon(Long gamesWon) {
        this.gamesWon = gamesWon;
    }
    public Long getTotalScore() {
        return totalScore == null ? 0L : totalScore;
    }
    public void setTotalScore(Long totalScore) {
        this.totalScore = totalScore;
    }
}
