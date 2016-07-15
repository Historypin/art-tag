package sk.eea.arttag.game.service;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.junit.After;
import org.junit.Before;
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
import sk.eea.arttag.repository.CulturalObjectRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.stream.IntStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.number.IsCloseTo.closeTo;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(TestApp.class)
@IntegrationTest
public class CardServiceTest {

    private static final Logger LOG = LoggerFactory.getLogger(CardServiceTest.class);

    public static final int NUMBER_OF_OBJECTS = 10;

    public static final int NUMBER_OF_EXECUTIONS = 100;

    @Autowired
    private CardService cardService;

    @Autowired
    private CulturalObjectRepository repository;

    private List<CulturalObject> culturalObjects = new ArrayList<>();

    @Before
    public void prepare() {
        IntStream.range(0, NUMBER_OF_OBJECTS).forEach(value -> {
            culturalObjects.add(repository.save(createCO()));
        });
    }

    @After
    public void tearDown() {
        repository.deleteInBatch(culturalObjects);
    }

    /**
     * Purpose of the test is to demonstrate that method getNextCulturalObject() will return different cards each time, and
     * is not susceptible to race-conditions which may occur.
     *
     * This test uses parralel stream execution technique to simulate race-conditions.
     */
    @Test
    public void testGetNextCulturalObjectByStreamMethod() {
        Multimap<Long, CulturalObject> multimap = ArrayListMultimap.create();
        IntStream.range(0, NUMBER_OF_EXECUTIONS).parallel().forEach(i -> {{
            CulturalObject co = cardService.getNextCulturalObject();
            if (co != null) {
                multimap.put(co.getId(), co);
            }
        }});

        double[] sizes = multimap.keySet().stream().mapToDouble(aLong -> (double) multimap.get(aLong).size()).toArray();
        StandardDeviation std = new StandardDeviation();
        assertThat(std.evaluate(sizes), is(closeTo(0.0, 0.5)));

    }

    /**
     * Purpose of the test is to demonstrate that method getNextCulturalObject() will return different cards each time, and
     * is not susceptible to race-conditions which may occur.
     *
     * This test uses countdown latch signal technique to simulate race-conditions.
     */
    @Test
    public void testGetNextCulturalObjectByCountDownLatchMethod() throws InterruptedException {
        final CountDownLatch startSignal = new CountDownLatch(1);
        final CountDownLatch resultsReadySignal = new CountDownLatch(NUMBER_OF_EXECUTIONS);
        final Multimap<Long, CulturalObject> multimap = ArrayListMultimap.create();
        for (int i = 0; i < NUMBER_OF_EXECUTIONS; i++) {
            new Thread(() -> {
                try {
                    startSignal.await();
                    CulturalObject co = cardService.getNextCulturalObject();
                    if (co != null) {
                        multimap.put(co.getId(), co);
                    }
                } catch (InterruptedException e) {
                   LOG.error("Thread exception: ", e);
                } finally {
                    resultsReadySignal.countDown();
                }
            }).start();
        }

        Thread.sleep(100); // give some time to create and start all threads
        startSignal.countDown(); // signal all workers

        resultsReadySignal.await();

        double[] sizes = multimap.keySet().stream().mapToDouble(aLong -> (double) multimap.get(aLong).size()).toArray();
        StandardDeviation std = new StandardDeviation();
        assertThat(std.evaluate(sizes), is(closeTo(0.0, 0.5)));
    }

    private static CulturalObject createCO() {
        CulturalObject co = new CulturalObject();
        co.setAuthor("author");
        co.setBatchId(1L);
        return co;
    }
}
