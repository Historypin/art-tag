package sk.eea.arttag.game.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sk.eea.arttag.game.model.Card;
import sk.eea.arttag.game.model.Game;
import sk.eea.arttag.game.model.GameEvent;
import sk.eea.arttag.game.model.GameStatus;
import sk.eea.arttag.game.model.Player;
import sk.eea.arttag.game.model.UserInput;

public class StateMachine {

	private static final Logger LOG = LoggerFactory.getLogger(StateMachine.class);

	private GameService gameService;

	public StateMachine(GameService gameService) {
		this.gameService = gameService;
	}

	public void triggerEvent(Game game, GameEvent event, UserInput userInput, String userToken) {
		//verify validity of userInput (if not null)

		switch (game.getStatus()) {
		case NEW:
			if (GameEvent.GAME_CREATED == event) {
				game.setStatus(GameStatus.CREATED);
				game.setEndOfRound(timeout(GameService.ROUND_LENGTH_IN_SECONDS));// FIXME:
				gameService.getGames().put(game.getId(), game);
			}
			break;

		case CREATED:
			if (GameEvent.PLAYER_JOINED == event) {
				// evaluate if userToken is already in joined players
				// evaluate number of users, possible we can start the game

			} else if (GameEvent.ROUND_STARTED == event) {
				game.setDeck(getInitialDeck());
				dealCards(game, GameService.HAND_SIZE);
				boolean canStartNextRound = nextRound(game);
				if (!canStartNextRound) {
					game.setStatus(GameStatus.FINISHED);
				} else {
					game.setEndOfRound(timeout(GameService.ROUND_LENGTH_IN_SECONDS));
					game.setStatus(GameStatus.ROUND_STARTED);
				}

			} else if (GameEvent.TIMEOUT == event) {
				// start the game? or discard game due to minimal number of
				// players not reached? or start a new timer?
				game.setEndOfRound(timeout(GameService.ROUND_LENGTH_IN_SECONDS));
				triggerEvent(game, GameEvent.ROUND_STARTED, null, null);
			}
			break;

		case ROUND_STARTED:
			if (GameEvent.TAGS_SELECTED == event) {
				processUserInput(game, userInput, userToken);
				if (true) {//verify tags set correctly
				}
				game.setEndOfRound(timeout(GameService.ROUND_LENGTH_IN_SECONDS));
				game.setStatus(GameStatus.ROUND_TAGS_SELECTED);

			} else if (GameEvent.TIMEOUT == event) {
				// dealer did not select any tags, start a new round
			}
			break;

		case ROUND_TAGS_SELECTED:
			if (GameEvent.PLAYER_OWN_CARD_SELECTED == event) {
				processUserInput(game, userInput, userToken);
				// check if all players already selected own card
				boolean notYetAllPlayersSelected = game.getPlayers().values().stream()
						.anyMatch(p -> p.getOwnCardSelection() == null);
				if (!notYetAllPlayersSelected) {
					game.setStatus(GameStatus.ROUND_OWN_CARDS_SELECTED);
					game.setEndOfRound(timeout(GameService.ROUND_LENGTH_IN_SECONDS));
					showTable(game);
				}
			} else if (GameEvent.TIMEOUT == event) {
				// timeout triggered before all players selected own cards
				game.setStatus(GameStatus.ROUND_OWN_CARDS_SELECTED);
				game.setEndOfRound(timeout(GameService.ROUND_LENGTH_IN_SECONDS));
				showTable(game);
			}
			break;

		case ROUND_OWN_CARDS_SELECTED:
			if (GameEvent.PLAYER_TABLE_CARD_SELECTED == event) {
				processUserInput(game, userInput, userToken);
				// check if all players already selected own card
				boolean notYetAllPlayersSelected = game.getPlayers().values().stream()
						.anyMatch(p -> p.getTableCardSelection() == null);
				if (!notYetAllPlayersSelected) {
					game.setStatus(GameStatus.ROUND_FINISHED);
					game.setEndOfRound(timeout(GameService.ROUND_LENGTH_IN_SECONDS));
					//finishRound();
				}
			} else if (GameEvent.TIMEOUT == event) {
				// timeout triggered before all players selected table cards
				game.setStatus(GameStatus.ROUND_FINISHED);
				game.setEndOfRound(timeout(GameService.ROUND_LENGTH_IN_SECONDS));
				//finishRound();
			}
			break;

/*		case ROUND_TABLE_CARDS_SELECTED:
			//finishRound();
			break;*/

		case ROUND_FINISHED:
/*			if (GameEvent.ROUND_STARTED == event) {
				game.setEndOfRound(timeout(GameService.ROUND_LENGTH_IN_SECONDS));
				game.setStatus(GameStatus.ROUND_STARTED);

			} else */if (GameEvent.TIMEOUT == event) {
				// start the game? or discard game due to minimal number of
				// players not reached? or start a new timer?
				boolean canStartNextRound = nextRound(game);
				if (!canStartNextRound) {
					game.setStatus(GameStatus.FINISHED);
				} else {
					game.setStatus(GameStatus.ROUND_STARTED);
					game.setEndOfRound(timeout(GameService.ROUND_LENGTH_IN_SECONDS));
				}

			} else if (GameEvent.PLAYER_READY_FOR_NEXT_ROUND == event) {

				processUserInput(game, userInput, userToken);
				if (true) {//verify all set correctly
				}

				boolean notYetAllPlayersSelected = game.getPlayers().values().stream()
						.anyMatch(p -> !p.isReadyForNextRound());
				if (!notYetAllPlayersSelected) {
					boolean canStartNextRound = nextRound(game);
					if (!canStartNextRound) {
						game.setStatus(GameStatus.FINISHED);
					} else {
						game.setStatus(GameStatus.ROUND_STARTED);
						game.setEndOfRound(timeout(GameService.ROUND_LENGTH_IN_SECONDS));
					}
				}
			}
			break;

		case FINISHED:
			break;

		default:
			break;
		}
	}

	private boolean nextRound(Game game) {

		game.resetRound();

		boolean dealerFound = false;
		boolean dealerAssigned = false;
		Iterator<Entry<String, Player>> it = game.getPlayers().entrySet().iterator();
		while (it.hasNext()) {
		    Map.Entry<String, Player> entry = it.next();
		    Player player = entry.getValue();
		    if (dealerFound) {
			    player.roundReset();
		    	player.setDealer(true);
		    	dealerAssigned = true;
		    } else {
			    dealerFound = player.isDealer();
			    player.roundReset();
		    }
		}

		if (!dealerAssigned) {
			it = game.getPlayers().entrySet().iterator();
			if (it.hasNext()) {
			    Map.Entry<String, Player> entry = it.next();
			    Player player = entry.getValue();
			    player.setDealer(true);
			} else {
				return false;
			}
		}

		boolean playerWithNoCardsExists = game.getPlayers().values().stream()
				.anyMatch(p -> p.getHand().size() < 1);
		if (playerWithNoCardsExists) {
			return false;
		}

		return true;
	}

	private void showTable(Game game) {
		List<Card> table = game.getPlayers().values().stream().filter(p -> p.getOwnCardSelection() != null).map(p -> p.getOwnCardSelection()).collect(Collectors.toList());
		game.setTable(table);
	}

	private void finishRound() {
		
	}

	private List<Card> getInitialDeck() {
		LOG.debug("INITIAL_DECK");
		List<Card> deck = IntStream.range(1, 30).mapToObj(i -> new Card(String.valueOf(i)))
				.collect(Collectors.toList());
		Collections.shuffle(deck);
		return deck;
	}

	private void dealCards(Game game, int numberOfCards) {
		LOG.debug("DEAL_CARDS");
		for (Player player : game.getPlayers().values()) {
			List<Card> cards = new ArrayList<>();
			for (int i = 0; i < numberOfCards; i++) {
				Card card = game.getDeck().remove(0);
				cards.add(card);
			}
			player.setHand(cards);
		}
	}

	private static Date timeout(int seconds) {
		return new Date(new Date().getTime() + (seconds * 1000));
	}

	private void processUserInput(Game game, UserInput input, String userToken) {

		// check userToken in current game
		Player player = game.getPlayers().get(userToken);
		if (player == null) {
			// ignore
			return;
		}

		// TODO check possible states for user input to be valid

		switch (input.getType()) {
		case TAGS_SELECTED:
			game.setTags(input.getValue());
			break;

		case OWN_CARD_SELECTED: {
			String cardToken = input.getValue();
			// find the card by token
			Card card = player.getHand().stream().filter(c -> cardToken.equalsIgnoreCase(c.getToken())).findFirst()
					.get();
			if (card == null) {
				// ignore
				return;
			}
			// pop the card out of players hand
			player.getHand().remove(card);
			// set to ownSelection
			player.setOwnCardSelection(card);
			break;
		}

		case TABLE_CARD_SELECTED: {
			String cardToken = input.getValue();
			// find the card by token
			Card card = game.getTable().stream().filter(c -> cardToken.equalsIgnoreCase(c.getToken())).findFirst().get();
			if (card == null) {
				// ignore
				return;
			}
			// set to tableSelection
			player.setTableCardSelection(card);
			break;
		}

		case PLAYER_READY_FOR_NEXT_ROUND : {
			player.setReadyForNextRound(true);
			break;
		}

		default:
			break;
		}
	}
}
