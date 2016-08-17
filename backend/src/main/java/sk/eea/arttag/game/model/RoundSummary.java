package sk.eea.arttag.game.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RoundSummary {

    private Game game;
    private Map<Long, Integer> playerSummary = new HashMap<>();
    private List<CardRoundSummary> cardSummary = new ArrayList<>();

    private static final Logger LOG = LoggerFactory.getLogger(RoundSummary.class);

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
        Map<Long, Integer> playerRoundSummaries = new HashMap<>();
        Card dealersCard = game.getPlayers().stream().filter(p -> p.isDealer()).findFirst().get().getOwnCardSelection();
        long numberOfPlayersSelectedTableCard = game.getPlayers().stream().filter(p -> p.getTableCardSelection() != null).count();
        if (dealersCard == null || numberOfPlayersSelectedTableCard == 0) {
            //INVALID ROUND
            //either dealer failed to select topic OR nobody voted for table card
            LOG.info("Invalid round, either dealer failed to select topic OR nobody voted for table card, game {}", game.getId());
            return;
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
            cardSummary.add(new CardRoundSummary(c.getCulturalObjectId(), cardScore));
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

    public Map<Long, Integer> getPlayerSummary() {
        return playerSummary;
    }
    public List<CardRoundSummary> getCardSummary() {
        return cardSummary;
    }
}
