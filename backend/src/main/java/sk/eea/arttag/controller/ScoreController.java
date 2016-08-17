package sk.eea.arttag.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import sk.eea.arttag.game.model.Game;
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
    public String score() {
        return "score";
    }

    private static Comparator<User> BY_USER_NAME = (e1, e2) -> e1.getNickName().compareTo(e2.getNickName());

    private static Comparator<User> BY_GAMES_PLAYED = (e1, e2) -> (int) ((e1.getPersonalScore() == null ? 0 : e1.getPersonalScore().getGamesPlayed())
            - (e2.getPersonalScore() == null ? 0 : e2.getPersonalScore().getGamesPlayed()));

    private static Comparator<User> BY_GAMES_WON = (e1, e2) -> (int) ((e1.getPersonalScore() == null ? 0 : e1.getPersonalScore().getGamesWon())
            - (e2.getPersonalScore() == null ? 0 : e2.getPersonalScore().getGamesWon()));

    private static Comparator<User> BY_TOTAL_SCORE = (e1, e2) -> (int) ((e1.getPersonalScore() == null ? 0 : e1.getPersonalScore().getTotalScore())
            - (e2.getPersonalScore() == null ? 0 : e2.getPersonalScore().getTotalScore()));

    @ResponseBody
    @RequestMapping(value = "/score/get.score", method = RequestMethod.GET)
    public DataTablesOutput<ScoreRow> getScore(@Valid DataTablesInput input) {
        DataTablesOutput<ScoreRow> output = new DataTablesOutput<>();
        output.setDraw(input.getDraw());

        //        List<User> records = new ArrayList<>();
        List<User> records = userRepository.findAll();
        output.setRecordsTotal((long) records.size());

        // search games
        final String searchTerm = input.getSearch().get(DataTablesInput.SearchCriteria.value);
        if (StringUtils.isNotEmpty(searchTerm)) {
            records = records.stream().filter(r -> r.getNickName().toLowerCase().contains(searchTerm.toLowerCase())).collect(Collectors.toList());
        }
        output.setRecordsFiltered((long) records.size());

        // sort by order
        final Optional<Map<DataTablesInput.OrderCriteria, String>> orderMapOptional = input.getOrder().stream().findFirst();
        if (orderMapOptional.isPresent()) {
            final Map<DataTablesInput.OrderCriteria, String> orderMap = orderMapOptional.get();
            switch (orderMap.get(DataTablesInput.OrderCriteria.column)) {
                case "0":
                    if ("asc".equals(orderMap.get(DataTablesInput.OrderCriteria.dir))) {
                        Collections.sort(records, BY_USER_NAME);
                    } else {
                        Collections.sort(records, Collections.reverseOrder(BY_USER_NAME));
                    }
                    break;
                case "1":
                    if ("asc".equals(orderMap.get(DataTablesInput.OrderCriteria.dir))) {
                        Collections.sort(records, BY_GAMES_PLAYED);
                    } else {
                        Collections.sort(records, Collections.reverseOrder(BY_GAMES_PLAYED));
                    }
                    break;
                case "2":
                    if ("asc".equals(orderMap.get(DataTablesInput.OrderCriteria.dir))) {
                        Collections.sort(records, BY_GAMES_WON);
                    } else {
                        Collections.sort(records, Collections.reverseOrder(BY_GAMES_WON));
                    }
                    break;
                case "3":
                    if ("asc".equals(orderMap.get(DataTablesInput.OrderCriteria.dir))) {
                        Collections.sort(records, BY_TOTAL_SCORE);
                    } else {
                        Collections.sort(records, Collections.reverseOrder(BY_TOTAL_SCORE));
                    }
                    break;
            }
        }

        // resolve pagination, list needs to be ordered already
        final int fromIndex = Math.max(0, input.getStart());
        final int toIndex = Math.min(records.size(), fromIndex + input.getLength());
        records = records.subList(fromIndex, toIndex);

        List<ScoreRow> data = records.stream().map(r -> {
            {
                ScoreRow s = new ScoreRow();
                s.setUserId(r.getId().toString());
                s.setUserName(r.getNickName());
                s.setGamesPlayed(r.getPersonalScore() == null ? null : r.getPersonalScore().getGamesPlayed());
                s.setGamesWon(r.getPersonalScore() == null ? null : r.getPersonalScore().getGamesWon());
                s.setTotalScore(r.getPersonalScore() == null ? null : r.getPersonalScore().getTotalScore());
                return s;
            }
        }).collect(Collectors.toList());

        output.setData(data);
        return output;
    }

}
