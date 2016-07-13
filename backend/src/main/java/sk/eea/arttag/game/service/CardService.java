package sk.eea.arttag.game.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import sk.eea.arttag.ApplicationProperties;
import sk.eea.arttag.game.model.Card;
import sk.eea.arttag.game.model.CardMetadata;
import sk.eea.arttag.model.CulturalObject;
import sk.eea.arttag.repository.CulturalObjectRepository;
import sk.eea.arttag.repository.TagRepository;

@Component
public class CardService {

    @Autowired
    private CulturalObjectRepository culturalObjectRepository;

    @Autowired
    private TagRepository tagRepository;

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
            CulturalObject co = culturalObjectRepository.findTop1ByOrderByLastSelectedAsc();
            if (co != null) {
                cards.add(culturalObject2Card(co, language));
            }
        }
        return cards;
    }

    private static Card culturalObject2Card(CulturalObject co, String language) {
        String token = UUID.randomUUID().toString();
        Long culturalObjectId = co.getId();
        String source = null;
        String descr = co.getDescriptionByLanguage(language, CARD_DESCRIPTION_DEFAULT_LANGUAGE);
        CardMetadata metadata = new CardMetadata(co.getAuthor(), co.getExternalUrl(), descr);
        Card c = new Card(token, culturalObjectId, source, metadata);
        return c;
    }
}
