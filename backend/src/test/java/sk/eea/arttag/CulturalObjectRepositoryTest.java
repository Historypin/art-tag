package sk.eea.arttag;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import sk.eea.arttag.model.CulturalObject;
import sk.eea.arttag.repository.CulturalObjectRepository;
import sk.eea.arttag.repository.custom.CulturalObjectRepositoryCustom;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(ArttagApp.class)
@ActiveProfiles("dev")
@Transactional
public class CulturalObjectRepositoryTest {

    @Autowired
    CulturalObjectRepositoryCustom repositoryCustom;
    @Autowired
    CulturalObjectRepository repository;

    private static final Logger LOG = LoggerFactory.getLogger(CulturalObjectRepositoryTest.class);

    @BeforeClass
    public static void setUp() {
    }

    @Test
    public void t() {
        for (int i = 0; i < 10; i++) {
            repository.save(createCO());
        }

        List<CulturalObject> cos = new ArrayList<>();
        IntStream.range(1, 100).parallel().forEach(i -> {{
            CulturalObject co = repositoryCustom.findOne();
            LOG.debug("{}", co == null ? null : co.getId());
            if (co != null) {
                cos.add(co);
            }
        }});

        LOG.debug("cos.size: {}", cos.size());
    }

    private static CulturalObject createCO() {
        CulturalObject co = new CulturalObject();
        co.setAuthor("author");
        co.setBatchId(1L);
        return co;
    }
}
