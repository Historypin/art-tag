package sk.eea.arttag.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import sk.eea.arttag.game.model.GameException;
import sk.eea.arttag.game.model.GamePlayerView;
import sk.eea.arttag.game.model.UserInput;
import sk.eea.arttag.game.service.GameService;
import sk.eea.arttag.model.User;
import sk.eea.arttag.repository.UserRepository;

import javax.websocket.EncodeException;
import java.io.IOException;
import java.net.URI;
import java.security.Principal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class WebSocketGameController extends TextWebSocketHandler {

    private static final String TOKEN = "token";
    private static final Logger LOG = LoggerFactory.getLogger(WebSocketGameController.class);

    private static final Map<String, WebSocketSession> clients = Collections.synchronizedMap(new HashMap<String, WebSocketSession>());

    @Autowired
    private GameService gameService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {

        String gameId = findGameIdInURI(session.getUri(), "gameId");
        LOG.debug("OPEN GAME_ID: {}", gameId);

        final Principal principal = session.getPrincipal();
        Long userId = null;
        if (principal != null) {
            User user = userRepository.findByEmail(principal.getName());
            if(user != null) {
                userId = user.getId();
            }
        }

        try {
            if(userId == null) {
                throw new GameException("UserId could not be found!");
            }
            clients.put(session.getId(), session);
            gameService.addPlayer(session.getId(), userId, gameId);
            LOG.info("Player: {} has connected", session.getPrincipal().getName());
        } catch (GameException e) {
            LOG.info("Failed to add player to game: {}", e.getMessage());
            clients.remove(session.getId());
            sendError(session.getId());
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        clients.remove(session.getId());
        gameService.removePlayer(session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        try {
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
                    case TOPIC_SELECTED:
                    case OWN_CARD_SELECTED:
                    case TABLE_CARD_SELECTED:
                    case PLAYER_READY_FOR_NEXT_ROUND:
                    case GAME_STARTED:
                        gameService.userInput(session.getId(), userInput);
                        break;
                    default:
                        LOG.error("UserInput type '{}' is not implemented yet!", userInput.getType());
                }
            }
            
        } catch (Exception e) {
            LOG.error("Invalid incoming message", e);
            throw e;
        }
    }

    @Scheduled(fixedRate = 1000)
    public void triggerGameTimeout() {
        gameService.getGames().values().stream().filter(g -> g.getRemainingTime() <= 0).forEach(g -> gameService.triggerGameTimeout(g));
        //LOG.debug("XXX Games: {}, Clients: {}", gameService.getGames().size(), clients.size());
    }

    public void trigger(List<GamePlayerView> views) {
        try {
            sendMessages(views);
        } catch (IOException | EncodeException e) {
            LOG.error("fuckup", e);
        } catch (RuntimeException re) {
            LOG.error("horrible fuckup", re);
        }
    }

    public void triggerClose(List<String> userTokens) {
        for (String userToken : userTokens) {
            WebSocketSession webSocketSession = clients.get(userToken);
            if (webSocketSession != null) {
                LOG.debug("Closing WS session for client {}", userToken);
                try {
                    webSocketSession.close(CloseStatus.NORMAL);
                } catch (IOException e) {
                    LOG.debug("Failed to close session for client {}", userToken);
                } finally {
                    clients.remove(userToken);
                }
            } else {
                LOG.debug("No session.");
            }
        }
    }

    private void sendMessages(List<GamePlayerView> gamePlayerViews) throws IOException, EncodeException {
        for (GamePlayerView view : gamePlayerViews) {
            String txt = objectMapper.writeValueAsString(view);
            LOG.debug("Serialized view: {}", txt);
            final TextMessage message = new TextMessage(txt);
            LOG.debug("Clients: {}", clients.size());
            final WebSocketSession webSocketSession = clients.get(view.getUserToken());
            if (webSocketSession != null) {
                LOG.debug("Sending message to client.");
                synchronized (webSocketSession) {
                    webSocketSession.sendMessage(message);
                }
            } else {
                LOG.debug("No session.");
            }
        }
    }

    private void sendError(String userToken) {
        //TODO
    }

    //    private static String marshall(GamePlayerView view) {
    //        JsonArrayBuilder handBuilder = Json.createArrayBuilder();
    ////        for (Card card : view.getHand()) {
    ////            handBuilder.add(Json.createObjectBuilder()
    ////                .add("token", card.getToken()));
    ////        }
    //        for (int i = 1; i <= 5; i++) {
    //            handBuilder.add(Json.createObjectBuilder().add("token", String.format("%02d.jpeg", i)));
    //        }
    //
    //        JsonArrayBuilder tableBuilder = Json.createArrayBuilder();
    ////        for (Card card : view.getGameView().getTable()) {
    ////            tableBuilder.add(Json.createObjectBuilder()
    ////                .add("token", card.getToken()));
    ////        }
    //
    //        for (int i = 1; i <= 5; i++) {
    //            tableBuilder.add(Json.createObjectBuilder().add("token", String.format("%02d.jpeg", i)));
    //        }
    //
    //        JsonArrayBuilder playersBuilder = Json.createArrayBuilder();
    ////        for (Player player : view.getGameView().getPlayers().values()) {
    //        for (Player player : view.getGameView().getPlayers()) {
    //        	playersBuilder.add(Json.createObjectBuilder()
    //                .add("name", player.getName())
    //                .add("dealer", player.isDealer())
    //                .add("readyForNextRound", player.isReadyForNextRound()));
    //        }
    //
    //        final String res = Json.createObjectBuilder()
    //            .add("userToken", view.getUserToken())
    //            .add("game", Json.createObjectBuilder()
    //                .add("id", view.getGameView().getId())
    //                .add("name", view.getGameView().getName())
    //                .add("remainingTime", view.getGameView().getRemainingTime())
    //                .add("tags", view.getGameView().getTags() != null ? view.getGameView().getTags() : "")
    //                .add("created", view.getGameView().getCreated().toString())
    //                .add("players", playersBuilder)
    //                .add("status", view.getGameView().getStatus().name()))
    //            .add("hand", handBuilder)
    //            .add("table", tableBuilder)
    //            .build()
    //            .toString();
    //
    //        return res;
    //    }

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
