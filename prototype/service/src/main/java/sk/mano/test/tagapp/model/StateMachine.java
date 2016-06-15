package sk.mano.test.tagapp.model;

public class StateMachine {

	public GameStatus next(Game game) {

		switch (game.getStatus()) {
		case CREATED:
			return GameStatus.ROUND_STARTED;

		case ROUND_STARTED:
			return GameStatus.ROUND_TAG_SELECTED;

		case ROUND_TAG_SELECTED:
			return GameStatus.ROUND_ALL_FINISHED;

		case ROUND_ALL_FINISHED:
			return GameStatus.ROUND_STARTED;

		case FINISHED:
			return null;

		default:
			return null;
		}
	}
}
