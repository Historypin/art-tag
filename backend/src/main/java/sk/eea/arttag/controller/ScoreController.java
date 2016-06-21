package sk.eea.arttag.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import sk.eea.arttag.model.Score;

import java.util.ArrayList;

@Controller
public class ScoreController {

    private ArrayList<Score> score = new ArrayList<>();


    @RequestMapping(value = "/score", method = RequestMethod.GET)
    public String score(Model model) {

        score.clear();

        Score s = new Score();
        s.setGamesPlayed(10L);
        s.setGamesWon(5L);
        s.setTotalScore(100L);
        score.add(s);

        Score ss = new Score();
        ss.setGamesPlayed(220L);
        ss.setGamesWon(253L);
        ss.setTotalScore(131L);
        score.add(ss);

        Score sss = new Score();
        sss.setGamesPlayed(9380L);
        sss.setGamesWon(213L);
        sss.setTotalScore(143L);
        score.add(ss);

        Score ssss = new Score();
        ssss.setGamesPlayed(10234L);
        ssss.setGamesWon(5234L);
        ssss.setTotalScore(13234L);
        score.add(ssss);

        Score sssss = new Score();
        sssss.setGamesPlayed(100L);
        sssss.setGamesWon(50L);
        sssss.setTotalScore(1000L);
        score.add(sssss);

        model.addAttribute("scores", score);
        return "score";
    }


}
