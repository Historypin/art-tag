package sk.eea.arttag.repository;

import java.util.Date;

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
import sk.eea.arttag.model.CulturalObject;
import sk.eea.arttag.model.LocalizedString;
import sk.eea.arttag.model.Tag;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(TestApp.class)
@IntegrationTest
@Transactional
public class TagRepositoryTest {

    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private CulturalObjectRepository culturalObjectRepository;

    private static final Logger LOG = LoggerFactory.getLogger(TagRepositoryTest.class);

    @Test
    public void test() {
        long tags = tagRepository.count();
        long cos = culturalObjectRepository.count();
        LOG.debug("Tags: {}, Cos: {}", tags, cos);

        CulturalObject co = culturalObjectRepository.save(createCO());
        long newCoId = co.getId();

        tags = tagRepository.count();
        cos = culturalObjectRepository.count();
        LOG.debug("Tags: {}, Cos: {}", tags, cos);

        CulturalObject co2 = culturalObjectRepository.findOne(newCoId);
        if (co != null) {
/*            Tag tag = new Tag() {{
                setCreated(new Date());
                setCulturalObject(co2);
                setHitScore(1L);
                LocalizedString ls = new LocalizedString() {{
                    setLanguage("en");
                    setValue("blah");
                }};
                setValue(ls);
            }};*/

            Tag tag = new Tag();
            tag.setCreated(new Date());
            tag.setCulturalObject(co2);
            tag.setHitScore(1f);
            LocalizedString ls = new LocalizedString();
            ls.setLanguage("en");
            ls.setValue("blah");
            tag.setValue(ls);

            tagRepository.save(tag);
        }

        tags = tagRepository.count();
        cos = culturalObjectRepository.count();
        LOG.debug("Tags: {}, Cos: {}", tags, cos);
    }

    private static CulturalObject createCO() {
        CulturalObject co = new CulturalObject();
        co.setAuthor("author");
        co.setBatchId(1L);
        return co;
    }

}
