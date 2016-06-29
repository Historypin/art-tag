package sk.eea.arttag.game.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import sk.eea.arttag.ApplicationProperties;
import sk.eea.arttag.game.model.Card;
import sk.eea.arttag.game.model.CardMetadata;

@Component
public class CardService {

    @Autowired
    private ApplicationProperties applicationProperties;

    private static final Logger LOG = LoggerFactory.getLogger(CardService.class);

    public List<Card> getCards(int numberOfCards) {

        List<Card> deck = new ArrayList<>();

        //TODO call CardService.getCards()
        for (int i = 0; i <= numberOfCards; i++) {
            final String cardToken = String.format("%02d.jpeg", i);
            final String cardSource = String.format("%s://%s/%s/%s", applicationProperties.getHostnamePrefix(), applicationProperties.getHostname(),
                    applicationProperties.getCulturalObjectsPublicPath(), cardToken);
            deck.add(new Card(cardToken, cardSource, new CardMetadata("author", "externalUrl", "description")));
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
}
