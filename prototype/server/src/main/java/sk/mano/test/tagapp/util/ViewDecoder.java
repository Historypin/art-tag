package sk.mano.test.tagapp.util;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

import sk.mano.test.tagapp.model.GamePlayerView;

public class ViewDecoder implements Decoder.Text<GamePlayerView> {

	@Override
	public void destroy() {
	}

	@Override
	public void init(EndpointConfig arg0) {
		System.out.println("dec init");
	}

	@Override
	public GamePlayerView decode(String arg0) throws DecodeException {
		System.out.println("dec dec");
		return null;
	}

	@Override
	public boolean willDecode(String arg0) {
		System.out.println("dec will");
		return false;
	}

}
