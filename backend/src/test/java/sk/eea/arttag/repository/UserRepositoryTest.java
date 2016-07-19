package sk.eea.arttag.repository;

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
import sk.eea.arttag.model.Score;
import sk.eea.arttag.model.User;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(TestApp.class)
@IntegrationTest
@Transactional
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private static final String USER_ID = "user1";

    private static final Logger LOG = LoggerFactory.getLogger(UserRepositoryTest.class);

    @Test
    public void test() {
        User u1 = userRepository.findOne(USER_ID);
        Score s1 = u1.getPersonalScore();
        LOG.debug("Played: {}, Won: {}, Total: {}", (s1 == null ? null : s1.getGamesPlayed()), (s1 == null ? null : s1.getGamesWon()), (s1 == null ? null : s1.getTotalScore()));

        Score s2 = new Score();
        s2.setGamesPlayed(10L);
        s2.setGamesWon(5L);
        s2.setTotalScore(50L);
        u1.setPersonalScore(s2);
        userRepository.save(u1);

        User u3 = userRepository.findOne(USER_ID);
        Score s3 = u3.getPersonalScore();
        LOG.debug("Played: {}, Won: {}, Total: {}", (s3 == null ? null : s3.getGamesPlayed()), (s3 == null ? null : s3.getGamesWon()), (s3 == null ? null : s3.getTotalScore()));
    }

}
