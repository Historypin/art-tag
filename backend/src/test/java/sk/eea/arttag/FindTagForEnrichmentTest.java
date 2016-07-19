package sk.eea.arttag;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import sk.eea.arttag.model.CulturalObject;
import sk.eea.arttag.model.LocalizedString;
import sk.eea.arttag.model.Tag;
import sk.eea.arttag.repository.CulturalObjectRepository;
import sk.eea.arttag.repository.TagRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(TestApp.class)
@IntegrationTest
@Transactional
public class FindTagForEnrichmentTest {

    private static final Logger LOG = LoggerFactory.getLogger(FindTagForEnrichmentTest.class);

    @Autowired
    private CulturalObjectRepository coRepo;

    @Autowired
    private TagRepository tagRepo;
    
    private Map<String, Float> avgHitScoreMap = new HashMap<>();

    /**
     * A. Test NEFUNGUJE
     *  1. insert testovacich dat do DB
     *  2. select testovacich dat
     *  3. kontrola: selectnute data su chybne (nespravne vypocitany priemer z hit_score, ako keby neurobil AVG(hit_score) ale zoberie prvy zaznam z grupy))
     * B. Test FUNGUJE
     *  1. insert vynechame, pouzijeme data z predchadzajuceho spustenia testu (samozrejme nesmel byt rollback)
     *  2. select testovacich dat
     *  3. kontrola: vsetko je OK
     */
    @Test
    @Ignore
    //@Rollback(false)
    public void testFindTagsForEnrichment() {
        try {
            SimpleDateFormat f = new SimpleDateFormat("yyyy-M-d");
            Long batchId = -1213L;

            // insert test data
            insertTestData(batchId);
            
            Date fromDate = f.parse("2000-1-1");
            Date untilDate = f.parse("2030-1-1");
            Float tagThreshold = 0.49999f; // 0.5f;
            int offset = 0;
            int pageSize = 222;
            
            // find
            Integer count = tagRepo.countTagsForEnrichment(fromDate, untilDate, batchId, tagThreshold);
            List<Tag> tags = tagRepo.findTagsForEnrichment(fromDate, untilDate, batchId, tagThreshold, offset, pageSize);

            //Assert.assertEquals("count", 2, count.intValue()); // tagThreshold je nastaveny tak, ze by mali byt vratene dva zaznamy
            //Assert.assertEquals("tags.size()", 2, tags.size());

            LOG.info("--------------------------");
            LOG.info("count: " + count);
            LOG.info("--------------------------");
            LOG.info("tags size: " + tags.size());
            LOG.info("--------------------------");
            for (Tag tag: tags) {
                LOG.info("tag value: " + tag.getValue().getLanguage() + ", " + tag.getValue().getValue());
                LOG.info("avgHitScore: " + tag.getHitScore());
                LOG.info("--------------------------");
                
                String key = tag.getValue().getLanguage() + "," + tag.getValue().getValue();
                //Assert.assertEquals("", avgHitScoreMap.get(key), tag.getHitScore());
            }
            
        } catch (Exception e) {
            LOG.error("", e);
        }
    }
    
    private void insertTestData(Long batchId) {
        Calendar cal = GregorianCalendar.getInstance();
        List<Tag> tags = new ArrayList<>();
        
        cal.add(Calendar.SECOND, 1);
        tags.add(tag("sk", "tag1", 0.1f, cal.getTime()));
        tags.add(tag("sk", "tag1", 0.2f, cal.getTime()));
        tags.add(tag("sk", "tag1", 0.3f, cal.getTime()));
        avgHitScoreMap.put("sk,tag1", 0.2f);
                
        cal.add(Calendar.SECOND, 1);
        tags.add(tag("sk", "tag2", 0.4f, cal.getTime()));
        tags.add(tag("sk", "tag2", 0.5f, cal.getTime()));
        tags.add(tag("sk", "tag2", 0.6f, cal.getTime()));
        avgHitScoreMap.put("sk,tag2", 0.5f);
                
        cal.add(Calendar.SECOND, 1);
        tags.add(tag("en", "tag1", 0.2f, cal.getTime()));
        tags.add(tag("en", "tag1", 0.3f, cal.getTime()));
        tags.add(tag("en", "tag1", 0.4f, cal.getTime()));
        avgHitScoreMap.put("en,tag1", 0.3f);
                
        cal.add(Calendar.SECOND, 1);
        tags.add(tag("en", "tag2", 0.5f, cal.getTime()));
        tags.add(tag("en", "tag2", 0.6f, cal.getTime()));
        tags.add(tag("en", "tag2", 0.7f, cal.getTime()));
        avgHitScoreMap.put("en,tag2", 0.6f);

        CulturalObject co = new CulturalObject();
        
        co.setActive(true);
        co.setAuthor("test");
        co.setBatchId(batchId);
        co.setDescription(Arrays.asList(new LocalizedString("sk", "co1")));
        // co.setExternalId(externalId);
        co.setExternalSource("externalSource1");
        co.setExternalUrl("externalUrl1");
        co.setLastSelected(new Date());
        co.setNumberOfSelections(1);
        co.setPublicSource("publicSource1");
        co.setTags(tags);

        coRepo.saveAndFlush(co);
    }

    private static Tag tag(String language, String tag, Float hitScore, Date created) {
        Tag t = new Tag();
        t.setCreated(created);
        t.setValue(new LocalizedString(language, tag));
        t.setHitScore(hitScore);
        return t;
    }

}
