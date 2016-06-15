package sk.mano.test.tagapp.util;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

import sk.mano.test.tagapp.model.Card;
import sk.mano.test.tagapp.model.GamePlayerView;
import sk.mano.test.tagapp.model.Player;

public class ViewEncoder implements Encoder.Text<GamePlayerView> {

	@Override
	public void destroy() {
	}

	@Override
	public void init(EndpointConfig arg0) {
		System.out.println("enc init");
	}

	@Override
	public String encode(GamePlayerView view) throws EncodeException {
		System.out.println("enc enc");

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

		System.out.println(res);
		return res;
	}

}
