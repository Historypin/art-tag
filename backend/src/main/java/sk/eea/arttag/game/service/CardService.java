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

    //TODO: this will be replaced by the getCard method
    public List<Card> getCards(int numberOfCards) {

        List<Card> deck = new ArrayList<>();

        //TODO call CardService.getCards()
        for (int i = 0; i <= numberOfCards; i++) {
            final String cardToken = UUID.randomUUID().toString();
            final Long culturalObjectId = 1L;
            final String img = String.format("%02d.jpeg", i);
            /*final String cardSource = String.format("%s://%s/%s/%s", applicationProperties.getHostnamePrefix(), applicationProperties.getHostname(),
                applicationProperties.getCulturalObjectsPublicPath(), img);*/

            final String cardSource = String.format("http://dummyimage.com/%dx%d/000/fff.png", new Random().nextInt((1600 - 600) + 1), new Random().nextInt((1600 - 600) + 1));

            //TODO: replace dummies
            deck.add(new Card(cardToken, culturalObjectId, cardSource, new CardMetadata("author", "externalUrl", "description")));
        }
        Collections.shuffle(deck);
        return deck;
    }

/*    private Card getCard(Game game) {
        Random random = new Random();
        String cardToken;
        do {
            cardToken = String.format("%02d.jpeg", random.nextInt(12) + 1);
        } while (!isUnique(cardToken, game));
        final String cardSource = String.format("%s://%s/%s/%s", applicationProperties.getHostnamePrefix(), applicationProperties.getHostname(),
                applicationProperties.getCulturalObjectsPublicPath(), cardToken);
        return new Card(cardToken, cardSource, new CardMetadata("author", "externalUrl", "description"));
    }

    private boolean isUnique(String cardToken, Game game) {
        for (Player player : game.getPlayers()) {
            if (player.getHand().stream().anyMatch(card -> card.getToken().equals(cardToken))) {
                return false;
            }
        }
        return true;
    }*/

    //TODO: this will be the new version of getCards when the database is not empty
    public List<Card> getCard(int numberOfCards, String language) {

        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < numberOfCards; i++) {
            CulturalObject co = culturalObjectRepository.findTopByOrderByLastSelectedAsc();
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
                Tag tag = new Tag() {{
                    setCreated(new Date());
                    setCulturalObject(co);
                    setHitScore((float) s.getScore() / maxScore);
                    LocalizedString ls = new LocalizedString() {{
                        setLanguage(lang);
                        setValue(tags);
                    }};
                    setValue(ls);
                }};
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
            return;
        }
        int max = optional.get().getGameScore();
        if (max == 0) {
            //ignore
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
                LOG.debug("Updating user: {}", user.getLogin());
                userRepository.save(user);
            }
        });
    }

    public synchronized CulturalObject getNextCulturalObject() {
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
        String source = null;
        String descr = co.getDescriptionByLanguage(language, CARD_DESCRIPTION_DEFAULT_LANGUAGE);
        CardMetadata metadata = new CardMetadata(co.getAuthor(), co.getExternalUrl(), descr);
        return new Card(token, culturalObjectId, source, metadata);
    }

}
