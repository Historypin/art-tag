package sk.eea.arttag.game.model;

import java.util.ArrayList;
import java.util.List;

public class RoundSummary {

    private Game game;
    private List<PlayerRoundSummary> playerRoundSummaries = new ArrayList<>();

    private RoundSummary(Game game, List<PlayerRoundSummary> playerRoundSummaries) {
        this.game = game;
        this.playerRoundSummaries = playerRoundSummaries;
    }

    public static RoundSummary create(Game game) {
        List<PlayerRoundSummary> playerRoundSummaries = evaluateRound(game);
        RoundSummary summary = new RoundSummary(game, playerRoundSummaries);
        return summary;
    }

    private static List<PlayerRoundSummary> evaluateRound(Game game) {
        List<PlayerRoundSummary> playerRoundSummaries = new ArrayList<>();
        for (Player player : game.getPlayers()) {
            playerRoundSummaries.add(new PlayerRoundSummary(player.getUserId(), 0));
        }
        return playerRoundSummaries;
    }

    public Game getGame() {
        return game;
    }

    public List<PlayerRoundSummary> getPlayerRoundSummaries() {
        return playerRoundSummaries;
    }
}
