package sk.eea.arttag.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import sk.eea.arttag.model.User;
import sk.eea.arttag.model.datatables.DataTablesInput;
import sk.eea.arttag.model.datatables.DataTablesOutput;
import sk.eea.arttag.model.datatables.ScoreRow;
import sk.eea.arttag.repository.UserRepository;

@Controller
public class ScoreController {

    @Autowired
    private UserRepository userRepository;

    @RequestMapping(value = "/score", method = RequestMethod.GET)
    public String score(Model model, Principal principal) {
        return "score";

/*        score.clear();

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
        return "score";*/
    }

    @ResponseBody
    @RequestMapping(value = "/score/get.score", method = RequestMethod.GET)
    public DataTablesOutput<ScoreRow> getScore(@Valid DataTablesInput input) {
        DataTablesOutput<ScoreRow> output = new DataTablesOutput<>();
        output.setDraw(input.getDraw());

//        List<User> records = new ArrayList<>();
        List<User> records = userRepository.findAll();
        output.setRecordsTotal((long) records.size());

        // search games
/*        final String searchTerm = input.getSearch().get(DataTablesInput.SearchCriteria.value);
        if (isNotEmpty(searchTerm)) {
            publicGames = publicGames.stream()
                    .filter(game -> game.getName().toLowerCase().contains(searchTerm.toLowerCase()))
                    .collect(Collectors.toList());
        }
        output.setRecordsFiltered((long) publicGames.size());*/

        // sort by order
/*        final Optional<Map<DataTablesInput.OrderCriteria, String>> orderMapOptional = input.getOrder().stream().findFirst();
        if (orderMapOptional.isPresent()) {
            final Map<DataTablesInput.OrderCriteria, String> orderMap = orderMapOptional.get();
            switch (orderMap.get(DataTablesInput.OrderCriteria.column)) {
                case "0":
                    if ("asc".equals(orderMap.get(DataTablesInput.OrderCriteria.dir))) {
                        Collections.sort(publicGames, BY_NAME);
                    } else {
                        Collections.sort(publicGames, Collections.reverseOrder(BY_NAME));
                    }
                    break;
                case "1":
                    if ("asc".equals(orderMap.get(DataTablesInput.OrderCriteria.dir))) {
                        Collections.sort(publicGames, BY_NUM_OF_PLAYERS);
                    } else {
                        Collections.sort(publicGames, Collections.reverseOrder(BY_NUM_OF_PLAYERS));
                    }
                    break;
            }
        }*/

        // resolve pagination, list needs to be ordered already
        final int fromIndex = Math.max(0, input.getStart());
        final int toIndex = Math.min(records.size(), fromIndex + input.getLength());
        records = records.subList(fromIndex, toIndex);

        List<ScoreRow> data = records.stream().map(r -> {{
            ScoreRow s = new ScoreRow();
            s.setUserId(r.getLogin());
            s.setUserName(r.getNickName());
            s.setGamesPlayed(r.getPersonalScore() == null ? 0L : r.getPersonalScore().getGamesPlayed());
            s.setGamesWon(r.getPersonalScore() == null ? 0L : r.getPersonalScore().getGamesWon());
            s.setTotalScore(r.getPersonalScore() == null ? 0 : r.getPersonalScore().getTotalScore());
            return s;
        }}).collect(Collectors.toList());

        output.setData(data);
        return output;
    }

}
