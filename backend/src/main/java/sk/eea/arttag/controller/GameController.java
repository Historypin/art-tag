package sk.eea.arttag.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import sk.eea.arttag.game.model.Card;
import sk.eea.arttag.game.model.GamePlayerView;
import sk.eea.arttag.game.model.Player;
import sk.eea.arttag.game.service.GameService;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.websocket.EncodeException;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameController extends TextWebSocketHandler {

    private static final String TOKEN = "token";
    private static Map<String, WebSocketSession> clients = Collections.synchronizedMap(new HashMap<String, WebSocketSession>());

    private static final Logger LOG = LoggerFactory.getLogger(GameController.class);

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        clients.put(session.getId(), session);
        GameService.getInstance().addPlayer(session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        clients.remove(session.getId());
        GameService.getInstance().removePlayer(session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        synchronized(clients){
            // Iterate over the connected sessions
            // and broadcast the received message
            for(WebSocketSession client : clients.values()){
                if (!client.equals(session)){
                    String fmt = String.format("(%s -> %s) '%s'", session.getId(), client.getId(), message);
                    LOG.debug(fmt);
                    client.sendMessage(new TextMessage(fmt));
                }
            }
        }
    }

    public static Runnable JOB = new Runnable() {
        @Override
        public void run() {
            LOG.debug("Timer fired");
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

    private static GamePlayerView unmarshall(String message) {
        throw new NotImplementedException();
    }

    private static String marshall(GamePlayerView view) {
        JsonArrayBuilder handBuilder = Json.createArrayBuilder();
        for(Card card : view.getHand()) {
            handBuilder.add(Json.createObjectBuilder()
                    .add("Token", card.getToken()));
        }

        JsonArrayBuilder playersBuilder = Json.createArrayBuilder();
        for(Player player : view.getGameView().getPlayers()) {
            playersBuilder.add(Json.createObjectBuilder()
                    .add("Name", player.getName()));
        }

        String res = Json.createObjectBuilder()
                .add("UserToken", view.getUserToken())
                .add("Game", Json.createObjectBuilder()
                        .add("Name", view.getGameView().getName())
                        .add("RemainingTime", view.getGameView().getRemainingTime())
                        .add("Tags", view.getGameView().getTags() != null ? view.getGameView().getTags() : "")
                        .add("Created", view.getGameView().getCreated().toString())
                        .add("Players", playersBuilder)
                )
                //.add("hand", view.getHand())
                .add("Hand", handBuilder)
                .build()
                .toString();

        return res;
    }
}
