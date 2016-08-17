package sk.eea.arttag.repository;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import sk.eea.arttag.TestApp;
import sk.eea.arttag.model.CulturalObject;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(TestApp.class)
@IntegrationTest
public class CulturalObjectRepositoryTest {

    @Autowired
    private CulturalObjectRepository culturalObjectRepository;

    private static final Logger LOG = LoggerFactory.getLogger(CulturalObjectRepositoryTest.class);

    @Test
    @Ignore
    public void test() {

        CulturalObject co1 = culturalObjectRepository.findTopByOrderByLastSelectedAsc();
        LOG.debug("co1: {}", co1.getId());
        CulturalObject co2 = culturalObjectRepository.findTopByIdNotInOrderByLastSelectedAsc(Arrays.asList(new Long[] {co1.getId()}));
        LOG.debug("co2: {}", co2.getId());
        Assert.assertNotEquals(co1.getId(), co2.getId());
    }
}
