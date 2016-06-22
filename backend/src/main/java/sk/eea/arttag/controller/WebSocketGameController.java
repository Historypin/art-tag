package sk.eea.arttag.controller;

import java.io.IOException;
import java.net.URI;
import java.security.Principal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.websocket.EncodeException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

import sk.eea.arttag.game.model.Card;
import sk.eea.arttag.game.model.GamePlayerView;
import sk.eea.arttag.game.model.Player;
import sk.eea.arttag.game.model.UserInput;
import sk.eea.arttag.game.service.GameService;

public class WebSocketGameController extends TextWebSocketHandler {

    private static final String TOKEN = "token";
    private static final Map<String, WebSocketSession> clients = Collections
        .synchronizedMap(new HashMap<String, WebSocketSession>());

    private static final Logger LOG = LoggerFactory.getLogger(WebSocketGameController.class);

    @Autowired
    private ObjectMapper objectMapper;

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
        LOG.info("Player: {} has connected", session.getPrincipal().getName());
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
        synchronized (clients) {
            final String payload = message.getPayload();
            LOG.debug("Input received from client: {}, input: {}", session.getPrincipal().getName(), payload);
            final UserInput userInput = objectMapper.readValue(message.getPayload(), UserInput.class);
            switch (userInput.getType()) {
                case CHAT_MESSAGE:
                    // Iterate over the connected sessions
                    // and broadcast the received message
                    for (WebSocketSession client : clients.values()) {
                        if (!client.equals(session)) {
                            userInput.setPlayerName(session.getPrincipal().getName());
                            client.sendMessage(new TextMessage(objectMapper.writeValueAsString(userInput)));
                        }
                    }
                    break;
                case TAGS_SELECTED:
                case OWN_CARD_SELECTED:
                case TABLE_CARD_SELECTED:
                case PLAYER_READY_FOR_NEXT_ROUND:
                    GameService.getInstance().userInput(session.getId(), userInput);
                    break;
                default:
                    LOG.error("UserInput type '{}' is not implemented yet!", userInput.getType());
            }
        }
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
            //LOG.debug("Sending message to player {}", token);
            //LOG.debug("view: {}", view);
            final TextMessage message = new TextMessage(marshall(view));
            clients.get(token).sendMessage(message);
        }
    }

    private static String marshall(GamePlayerView view) {
        JsonArrayBuilder handBuilder = Json.createArrayBuilder();
        for (Card card : view.getHand()) {
            handBuilder.add(Json.createObjectBuilder()
                .add("token", card.getToken()));
        }

        JsonArrayBuilder tableBuilder = Json.createArrayBuilder();
        for (Card card : view.getGameView().getTable()) {
            tableBuilder.add(Json.createObjectBuilder()
                .add("token", card.getToken()));
        }

        JsonArrayBuilder playersBuilder = Json.createArrayBuilder();
        for (Player player : view.getGameView().getPlayers().values()) {
            playersBuilder.add(Json.createObjectBuilder()
                .add("name", player.getName())
                .add("dealer", player.isDealer())
                .add("readyForNextRound", player.isReadyForNextRound()));
        }

        final String res = Json.createObjectBuilder()
            .add("userToken", view.getUserToken())
            .add("game", Json.createObjectBuilder()
                .add("id", view.getGameView().getId())
                .add("name", view.getGameView().getName())
                .add("remainingTime", view.getGameView().getRemainingTime())
                .add("tags", view.getGameView().getTags() != null ? view.getGameView().getTags() : "")
                .add("created", view.getGameView().getCreated().toString())
                .add("players", playersBuilder)
                .add("status", view.getGameView().getStatus().name()))
            .add("hand", handBuilder)
            .add("table", tableBuilder)
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
