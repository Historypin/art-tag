package sk.eea.arttag.game.model;

public class RoundSummary {

	public RoundSummary() {
	}

	public static RoundSummary create(Game game) {
		RoundSummary summary = new RoundSummary();
		for (Player player : game.getPlayers()) {
			
		}
		return summary;
	}
}
