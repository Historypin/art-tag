package sk.mano.test.tagapp;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sk.mano.test.tagapp.model.GamePlayerView;
import sk.mano.test.tagapp.service.GameService;
import sk.mano.test.tagapp.util.ViewDecoder;
import sk.mano.test.tagapp.util.ViewEncoder;

@ServerEndpoint( value = "/game", encoders = ViewEncoder.class, decoders = ViewDecoder.class)
public class GameServerEndpoint {

	private static final String TOKEN = "token";
	private static Map<String, Session> clients = Collections.synchronizedMap(new HashMap<String, Session>());
//	private static Set<Session> clients = Collections.synchronizedSet(new HashSet<Session>());
	
	private static final Logger LOG = LoggerFactory.getLogger(GameServerEndpoint.class);

	public GameServerEndpoint() {
		LOG.debug("GameServerEndpoint constructor");
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
			clients.get(token).getBasicRemote().sendObject(view);
			LOG.debug("success");
		}
/*		for (Session s: clients.values()) {
			s.getBasicRemote().sendText("aaa");
		}*/
	}

	@OnMessage
    public void onMessage(String message, Session session) 
    	throws IOException {
		
		synchronized(clients){
			// Iterate over the connected sessions
			// and broadcast the received message
			for(Session client : clients.values()){
				if (!client.equals(session)){
					String fmt = String.format("(%s -> %s) '%s'", session.getId(), client.getId(), message);
					LOG.debug(fmt);
					client.getBasicRemote().sendText(fmt);
				}
			}
		}
		
    }
	
	@OnOpen
    public void onOpen (Session session) {
//		clients.add(session);
		clients.put(session.getId(), session);
		GameService.getInstance().addPlayer(session.getId());

		// Add session to the connected sessions set
/*		List<String> token = session.getRequestParameterMap().get(TOKEN);
		if (token != null && token.size() > 0) {
			clients.put(token.get(0), session);
		}*/
    }

    @OnClose
    public void onClose (Session session) {
//    	clients.remove(session);
    	clients.remove(session.getId());
		GameService.getInstance().removePlayer(session.getId());

    	// Remove session from the connected sessions set
/*		List<String> token = session.getRequestParameterMap().get(TOKEN);
		if (token != null && token.size() > 0) {
			clients.remove(token);
		}*/
    }
}
