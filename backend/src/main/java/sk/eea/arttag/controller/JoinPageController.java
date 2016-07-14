package sk.eea.arttag.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import sk.eea.arttag.game.model.Game;
import sk.eea.arttag.game.model.Player;
import sk.eea.arttag.game.service.GameService;
import sk.eea.arttag.model.datatables.DataTablesInput;
import sk.eea.arttag.model.datatables.DataTablesOutput;
import sk.eea.arttag.model.datatables.GameRow;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.swapCase;

@Controller
public class JoinPageController {

    @Autowired
    private GameService gameService;

    private static Comparator<Game> BY_NAME =
            (e1, e2) -> e1.getName().compareTo(e2.getName());

    private static Comparator<Game> BY_NUM_OF_PLAYERS =
            (e1, e2) -> e1.getPlayers().size() - e2.getPlayers().size();

    @RequestMapping("/join_page")
    public String getJoinPage() {
        return "join_page";
    }

    @ResponseBody
    @RequestMapping(value = "/join_page/get.games", method = RequestMethod.GET)
    public DataTablesOutput<GameRow> getGames(@Valid DataTablesInput input) {
        DataTablesOutput<GameRow> output = new DataTablesOutput<>();
        output.setDraw(input.getDraw());

        // filter out private games
        List<Game> publicGames = gameService.getGames().values().stream()
                .filter(game -> !game.isPrivateGame())
                .collect(Collectors.toList());
        output.setRecordsTotal((long) publicGames.size());

        // search games
        final String searchTerm = input.getSearch().get(DataTablesInput.SearchCriteria.value);
        if (isNotEmpty(searchTerm)) {
            publicGames = publicGames.stream()
                    .filter(game -> game.getName().toLowerCase().contains(searchTerm.toLowerCase()))
                    .collect(Collectors.toList());
        }
        output.setRecordsFiltered((long) publicGames.size());

        // sort by order
        final Optional<Map<DataTablesInput.OrderCriteria, String>> orderMapOptional = input.getOrder().stream().findFirst();
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
        }

        // resolve pagination, list needs to be ordered already
        final int fromIndex = Math.max(0, input.getStart());
        final int toIndex = Math.min(publicGames.size(), fromIndex + input.getLength());
        publicGames = publicGames.subList(fromIndex, toIndex);

        List<GameRow> data = publicGames.stream().map(game -> new GameRow(game.getId(), game.getName(), game.getPlayers().size())).collect(Collectors.toList());
        output.setData(data);
        return output;
    }

}
