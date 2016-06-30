package sk.eea.arttag.game.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class RoundSummary {

    private Game game;
    private Map<String, Integer> playerSummary = new HashMap<>();
    private List<CardRoundSummary> cardSummary = new ArrayList<>();

    private RoundSummary(Game game) {
        this.game = game;
    }

    public static RoundSummary create(Game game) {
        RoundSummary summary = new RoundSummary(game);
        summary.evaluateRound();
        return summary;
    }

    private static final int PLAYER_SCORE_WHEN_DEALER_0_OR_ALL = 2;
    private static final int DEALER_SCORE_WHEN_DEALER_NOT_0_OR_ALL = 3;
    private static final int PLAYER_SCORE_WHEN_CORRECT_AND_DEALER_NOT_0_OR_ALL = 3;

    private void evaluateRound() {
        Map<String, Integer> playerRoundSummaries = new HashMap<>();
        Card dealersCard = game.getPlayers().stream().filter(p -> p.isDealer()).findFirst().get().getOwnCardSelection();
        long numberOfPlayersSelectedTableCard = game.getPlayers().stream().filter(p -> p.getTableCardSelection() != null).count();
        if (numberOfPlayersSelectedTableCard == 0) {
            //TODO: invalid round
        }
        if (dealersCard.getPlayerSelections().size() == 0 || dealersCard.getPlayerSelections().size() == numberOfPlayersSelectedTableCard) {
            //all or no players selected dealers card
            game.getPlayers().forEach(p -> {
                int score = (p.isDealer() ? 0 : p.getTableCardSelection() != null ? PLAYER_SCORE_WHEN_DEALER_0_OR_ALL : 0);
                playerRoundSummaries.put(p.getUserId(), score);
            });
        } else {
            //otherwise
            game.getPlayers().forEach(p -> {
                int score = (p.isDealer() ? DEALER_SCORE_WHEN_DEALER_NOT_0_OR_ALL : dealersCard.equals(p.getTableCardSelection()) ? PLAYER_SCORE_WHEN_CORRECT_AND_DEALER_NOT_0_OR_ALL : 0);
                playerRoundSummaries.put(p.getUserId(), score);
            });
        }
        
        game.getTable().stream().forEach(c -> {

            int cardScore = 0;
            if (c.equals(dealersCard)) {
                //PLAYER SCORING: do not add more points to dealer

                //CARD SCORING
                cardScore = c.getPlayerSelections().size() + 1;
            } else {
                //PLAYER SCORING: has the player of this card selected a table card? if not, ignore him
                Optional<Player> op = game.getPlayers().stream().filter(p -> c.equals(p.getOwnCardSelection()) && p.getTableCardSelection() != null).findFirst();
                if (op.isPresent()) {
                    Player pl = op.get();
                    Integer plScore = playerRoundSummaries.get(pl.getUserId());
                    plScore += c.getPlayerSelections().size();
                    playerRoundSummaries.put(pl.getUserId(), plScore);

                    //CARD SCORING
                    cardScore = c.getPlayerSelections().size();
                }
            }

            //CARD SUMMARY
            cardSummary.add(new CardRoundSummary(c.getToken(), cardScore));
        });
        //PLAYER SUMMARY
        playerSummary = playerRoundSummaries;
/*        playerRoundSummaries.forEach((k, v) -> {
            playerSummary.add(new PlayerRoundSummary(k, v));
        });*/
    }

    public Game getGame() {
        return game;
    }

    public Map<String, Integer> getPlayerSummary() {
        return playerSummary;
    }
    public List<CardRoundSummary> getCardSummary() {
        return cardSummary;
    }
}
