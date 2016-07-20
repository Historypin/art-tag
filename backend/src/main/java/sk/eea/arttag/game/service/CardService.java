package sk.eea.arttag.game.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import sk.eea.arttag.ApplicationProperties;
import sk.eea.arttag.game.model.Card;
import sk.eea.arttag.game.model.CardMetadata;
import sk.eea.arttag.game.model.CardRoundSummary;
import sk.eea.arttag.game.model.Game;
import sk.eea.arttag.game.model.Player;
import sk.eea.arttag.model.CulturalObject;
import sk.eea.arttag.model.LocalizedString;
import sk.eea.arttag.model.Score;
import sk.eea.arttag.model.Tag;
import sk.eea.arttag.model.User;
import sk.eea.arttag.repository.CulturalObjectRepository;
import sk.eea.arttag.repository.TagRepository;
import sk.eea.arttag.repository.UserRepository;

@Component
public class CardService {

    @Autowired
    private CulturalObjectRepository culturalObjectRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ApplicationProperties applicationProperties;

    public static final String CARD_DESCRIPTION_DEFAULT_LANGUAGE = "en";
    private static final Logger LOG = LoggerFactory.getLogger(CardService.class);

    public List<Card> getCards(int numberOfCards, String language) {
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < numberOfCards; i++) {
            CulturalObject co = this.getNextCulturalObject();
            if (co != null) {
                cards.add(culturalObject2Card(co, language));
            }
        }
        return cards;
    }

    public void saveTags(List<CardRoundSummary> cardSummary, String tags, int playersCount, String lang) {
        LOG.debug("Saving tags");
        cardSummary.forEach(s -> {
            int maxScore = playersCount - 1;
            if (s.getScore() == 0 || s.getScore() == maxScore) {
                return;
            }
            CulturalObject co = culturalObjectRepository.findOne(s.getCulturalObjectId());
            if (co != null) {

                Tag tag = new Tag();
                tag.setCreated(new Date());
                tag.setCulturalObject(co);
                tag.setHitScore((float) s.getScore() / maxScore);
                LocalizedString ls = new LocalizedString();
                ls.setLanguage(lang);
                ls.setValue(tags);
                tag.setValue(ls);

                LOG.debug("Saving tag: {}", tags);
                tagRepository.save(tag);
            }
        });
    }

    public void updatePlayersAfterRoundFinished(Map<String, Integer> playerSummary) {
        LOG.debug("Updating players after round finished");
        playerSummary.forEach((k, v) -> {
            if (v == null || v == 0) {
                //ignore
            } else {
                User user = userRepository.findOne(k);
                if (user != null) {
                    Score score = user.getPersonalScore();
                    if (score == null) {
                        score = new Score();
                    }
                    score.setTotalScore(score.getTotalScore() == null ? v : score.getTotalScore() + v);
                    user.setPersonalScore(score);
                    LOG.debug("Updating user: {}", user.getLogin());
                    userRepository.save(user);
                }
            }
        });
    }

    public void updatePlayersAfterGameFinished(Game game) {
        LOG.info("Updating players after game finished");
        Optional<Player> optional = game.getPlayers().stream().max(Comparator.comparing(Player::getGameScore));
        if (!optional.isPresent()) {
            LOG.debug("No player found for game {}", game.getId());
            return;
        }
        int max = optional.get().getGameScore();
        if (max == 0) {
            //ignore
            LOG.debug("Max score is 0 for game {}", game.getId());
            return;
        }

        game.getPlayers().forEach(p -> {
            User user = userRepository.findOne(p.getUserId());
            if (user != null) {
                Score score = user.getPersonalScore();
                if (score == null) {
                    score = new Score();
                }
                score.setGamesPlayed(score.getGamesPlayed() == null ? 1 : score.getGamesPlayed() + 1);
                if (max == p.getGameScore()) {
                    score.setGamesWon(score.getGamesWon() == null ? 1 : score.getGamesWon() + 1);
                }
                user.setPersonalScore(score);
                LOG.debug("Updating user: {}", user.getLogin());
                userRepository.save(user);
            }
        });
    }

    protected synchronized CulturalObject getNextCulturalObject() {
        CulturalObject co = culturalObjectRepository.findTopByOrderByLastSelectedAsc();
        if (co != null) {
            co.setLastSelected(new Date());
            co.setNumberOfSelections(co.getNumberOfSelections() + 1);
            co = culturalObjectRepository.save(co);
        }
        return co;
    }

    private static Card culturalObject2Card(CulturalObject co, String language) {
        String token = UUID.randomUUID().toString();
        Long culturalObjectId = co.getId();
        String source = co.getPublicSource();
        String descr = co.getDescriptionByLanguage(language, CARD_DESCRIPTION_DEFAULT_LANGUAGE);
        CardMetadata metadata = new CardMetadata(co.getAuthor(), co.getExternalUrl(), descr);
        return new Card(token, culturalObjectId, source, metadata);
    }

}
