package sk.eea.arttag.controller;

import java.io.IOException;
import java.net.URI;
import java.security.Principal;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.websocket.EncodeException;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import sk.eea.arttag.game.model.Card;
import sk.eea.arttag.game.model.GamePlayerView;
import sk.eea.arttag.game.model.Player;
import sk.eea.arttag.game.model.UserInput;
import sk.eea.arttag.game.model.UserInputType;
import sk.eea.arttag.game.service.GameService;

public class WebSocketGameController extends TextWebSocketHandler {

    private static final String TOKEN = "token";
    private static Map<String, WebSocketSession> clients = Collections.synchronizedMap(new HashMap<String, WebSocketSession>());

    private static final Logger LOG = LoggerFactory.getLogger(WebSocketGameController.class);

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {

    	String gameId = findGameIdInURI(session.getUri(), "gameId");
    	LOG.debug("OPEN GAME_ID: {}", gameId);

    	Principal principal = session.getPrincipal();
    	if (principal == null) {
    		LOG.info("No principal");
    	} else {
    		LOG.info("Principal: {}", principal.getName());
    	}
        clients.put(session.getId(), session);
        GameService.getInstance().addPlayer(session.getId(), principal.getName(), gameId);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        clients.remove(session.getId());
        GameService.getInstance().removePlayer(session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

    	String payload = message.getPayload();
    	LOG.debug(payload);
    	UserInput ui = unmarshall(payload);
    	GameService.getInstance().userInput(session.getId(), ui);

/*        synchronized(clients){
            // Iterate over the connected sessions
            // and broadcast the received message
            for(WebSocketSession client : clients.values()){
                if (!client.equals(session)){
                    String fmt = String.format("(%s -> %s) '%s'", session.getId(), client.getId(), message);
                    LOG.debug(fmt);
                    client.sendMessage(new TextMessage(fmt));
                }
            }
        }*/
    }

    public static Runnable JOB = new Runnable() {
        @Override
        public void run() {
            List<GamePlayerView> games = GameService.getInstance().getGameViews();
            try {
                sendMessages(games);
            } catch (IOException | EncodeException e) {
                e.printStackTrace();
                LOG.error("fuckup", e);
            } catch (RuntimeException re) {
                re.printStackTrace();
                LOG.error("horrible fuckup", re);
            }
        }
    };

    private static void sendMessages(List<GamePlayerView> gamePlayerViews) throws IOException, EncodeException {
        for (GamePlayerView view : gamePlayerViews) {
            String token = view.getUserToken();
            LOG.debug("Sending message to player {}", token);
            //			clients.get(token).getBasicRemote().sendText(view.toString());
            LOG.debug("view: {}", view);
            final TextMessage message = new TextMessage(marshall(view));
            clients.get(token).sendMessage(message);
            LOG.debug("success");
        }
    }

    private static UserInput unmarshall(String message) {
    	JSONObject obj = new JSONObject(message);
    	String value = obj.getString("value");
    	String type = obj.getString("type");
    	String gameId = obj.getString("gameId");
    	UserInput ui = new UserInput();
    	ui.setGameId(gameId);
    	ui.setValue(value);
    	ui.setType(UserInputType.valueOf(type));
    	return ui;
    }

    private static String marshall(GamePlayerView view) {
        JsonArrayBuilder handBuilder = Json.createArrayBuilder();
        for(Card card : view.getHand()) {
            handBuilder.add(Json.createObjectBuilder()
                    .add("Token", card.getToken()));
        }

        JsonArrayBuilder tableBuilder = Json.createArrayBuilder();
        for(Card card : view.getGameView().getTable()) {
            tableBuilder.add(Json.createObjectBuilder()
                    .add("Token", card.getToken()));
        }

        JsonArrayBuilder playersBuilder = Json.createArrayBuilder();
        for(Player player : view.getGameView().getPlayers().values()) {
            playersBuilder.add(Json.createObjectBuilder()
                    .add("Name", player.getName())
                    .add("Dealer", player.isDealer())
                    .add("ReadyForNextRound", player.isReadyForNextRound()));
        }

        String res = Json.createObjectBuilder()
                .add("UserToken", view.getUserToken())
                .add("Game", Json.createObjectBuilder()
                        .add("Id", view.getGameView().getId())
                        .add("Name", view.getGameView().getName())
                        .add("RemainingTime", view.getGameView().getRemainingTime())
                        .add("Tags", view.getGameView().getTags() != null ? view.getGameView().getTags() : "")
                        .add("Created", view.getGameView().getCreated().toString())
                        .add("Players", playersBuilder)
                        .add("Table", tableBuilder)
                        .add("Status", view.getGameView().getStatus().name())
                )
                //.add("hand", view.getHand())
                .add("Hand", handBuilder)
                .build()
                .toString();

        return res;
    }

    private String findGameIdInURI(URI uri, String name) {
        Map<String, String> query_pairs = new HashMap<String, String>();
        String query = uri.getQuery();
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            query_pairs.put(pair.substring(0, idx), pair.substring(idx + 1));
        }
        return query_pairs.get(name);
    }
}
