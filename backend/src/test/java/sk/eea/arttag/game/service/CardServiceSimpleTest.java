package sk.eea.arttag.game.service;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import sk.eea.arttag.TestApp;
import sk.eea.arttag.game.model.CardRoundSummary;
import sk.eea.arttag.game.model.Game;
import sk.eea.arttag.game.model.GameTimeout;
import sk.eea.arttag.game.model.Player;
import sk.eea.arttag.model.*;
import sk.eea.arttag.repository.CulturalObjectRepository;
import sk.eea.arttag.repository.TagRepository;
import sk.eea.arttag.repository.UserRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(TestApp.class)
@IntegrationTest
@Transactional
public class CardServiceSimpleTest {

    @Autowired
    private CardService cardService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CulturalObjectRepository culturalObjectRepository;
    @Autowired
    private TagRepository tagRepository;

    private static final String USER_EMAIL = "user1@email.sk";
    private static final String USER_NAME = "Jozko Mrkvicka";
    private static final String TAGS = "tags";
    private static final String LANG = "lang";

    private static final Logger LOG = LoggerFactory.getLogger(CardServiceSimpleTest.class);

    @Test
    public void saveTags() {

        CulturalObject co = culturalObjectRepository.save(createCO());

        List<CardRoundSummary> cardSummary = new ArrayList<>();
        CardRoundSummary s = new CardRoundSummary(co.getId(), 10);
        cardSummary.add(s);
        cardService.saveTags(cardSummary, TAGS, 15, LANG);

        CulturalObject co2 = culturalObjectRepository.findOne(co.getId());
        List<Tag> tags1 = co2.getTags();
        //this is always null
        if (tags1 != null) {
            tags1.forEach(t -> LOG.debug("Tag1: {}", t));
        }

        List<Tag> tags2 = tagRepository.findAllByCulturalObjectId(co2.getId());
        if (tags2 != null) {
            tags2.forEach(t -> LOG.debug("Tag2 Created: {}, HitScore: {}, Lang: {}, Value: {}", t.getCreated(), t.getHitScore(), t.getValue().getLanguage(), t.getValue().getValue()));
        }
    }

    @Test
    public void updatePlayersAfterGameFinished() {
        User user = userRepository.save(createUser());

        User u1 = userRepository.findOne(user.getId());
        Score s1 = u1.getPersonalScore();
        LOG.debug("User: {}, Played: {}, Won: {}, Total: {}", u1.getId(), (s1 == null ? null : s1.getGamesPlayed()), (s1 == null ? null : s1.getGamesWon()), (s1 == null ? null : s1.getTotalScore()));

        Game game = createGame(user);
        cardService.updatePlayersAfterGameFinished(game);

        User u2 = userRepository.findOne(user.getId());
        Score s2 = u2.getPersonalScore();
        LOG.debug("User: {}, Played: {}, Won: {}, Total: {}", u2.getId(), (s2 == null ? null : s2.getGamesPlayed()), (s2 == null ? null : s2.getGamesWon()), (s2 == null ? null : s2.getTotalScore()));
    }

    private static Game createGame(User user) {
        GameTimeout gt = new GameTimeout(1, 1, 1, 1, 1);
        Game game = new Game("test1", "test1", 1, 2, true, user.getId(), gt);
        List<Player> players = new ArrayList<>();
        Player player = new Player("player1", "player 1", user.getId());
        player.setGameScore(10);
        players.add(player);
        game.setPlayers(players);
        return game;
    }

    private static CulturalObject createCO() {
        CulturalObject co = new CulturalObject();
        co.setAuthor("author");
        co.setBatchId(1L);
        return co;
    }

    private static User createUser() {
        User user = new User();
        user.setEmail(USER_EMAIL);
        user.setIdentityProviderType(IdentityProviderType.LOCAL);
        user.setNickName(USER_NAME);
        return user;
    }
}
