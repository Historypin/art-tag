package sk.eea.arttag.game.service;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import sk.eea.arttag.ArttagApp;
import sk.eea.arttag.game.model.Card;
import sk.eea.arttag.model.CulturalObject;
import sk.eea.arttag.repository.CulturalObjectRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(ArttagApp.class)
@ActiveProfiles("dev")
@Transactional
public class CardServiceTest {

    @Autowired
    private CulturalObjectRepository repository;
    @Autowired
    CardService cardService;

    @Test
    public void testCustomRepo() {

        System.out.println(repository.count());

        CulturalObject co = new CulturalObject();
        co.setAuthor("author");
        co.setBatchId(1L);
        repository.save(co);

        System.out.println(repository.count());

        List<Card> c2 = cardService.getCard(1, "en");
        Assert.assertEquals(1, c2.size());
        System.out.println(c2);
    }
}
