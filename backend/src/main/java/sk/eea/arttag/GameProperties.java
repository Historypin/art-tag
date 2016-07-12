package sk.eea.arttag;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("game")
public class GameProperties {

    private Integer handSize = 5;

    private Integer initialDeckSize = 100;

    private Integer minimumGamePlayers = 1;

    private Integer maximumGamePlayers = 3;

    private Integer timeoutGameCreated;

    private Integer timeoutRoundStarted;

    private Integer timeoutTopicSelected;

    private Integer timeoutOwnCardsSelected;

    private Integer timeoutRoundFinished;

    public Integer getHandSize() {
        return handSize;
    }

    public void setHandSize(Integer handSize) {
        this.handSize = handSize;
    }

    public Integer getInitialDeckSize() {
        return initialDeckSize;
    }

    public void setInitialDeckSize(Integer initialDeckSize) {
        this.initialDeckSize = initialDeckSize;
    }

    public Integer getMinimumGamePlayers() {
        return minimumGamePlayers;
    }

    public void setMinimumGamePlayers(Integer minimumGamePlayers) {
        this.minimumGamePlayers = minimumGamePlayers;
    }

    public Integer getMaximumGamePlayers() {
        return maximumGamePlayers;
    }

    public void setMaximumGamePlayers(Integer maximumGamePlayers) {
        this.maximumGamePlayers = maximumGamePlayers;
    }

    public Integer getTimeoutGameCreated() {
        return timeoutGameCreated;
    }

    public void setTimeoutGameCreated(Integer timeoutGameCreated) {
        this.timeoutGameCreated = timeoutGameCreated;
    }

    public Integer getTimeoutRoundStarted() {
        return timeoutRoundStarted;
    }

    public void setTimeoutRoundStarted(Integer timeoutRoundStarted) {
        this.timeoutRoundStarted = timeoutRoundStarted;
    }

    public Integer getTimeoutTopicSelected() {
        return timeoutTopicSelected;
    }

    public void setTimeoutTopicSelected(Integer timeoutTopicSelected) {
        this.timeoutTopicSelected = timeoutTopicSelected;
    }

    public Integer getTimeoutOwnCardsSelected() {
        return timeoutOwnCardsSelected;
    }

    public void setTimeoutOwnCardsSelected(Integer timeoutOwnCardsSelected) {
        this.timeoutOwnCardsSelected = timeoutOwnCardsSelected;
    }

    public Integer getTimeoutRoundFinished() {
        return timeoutRoundFinished;
    }

    public void setTimeoutRoundFinished(Integer timeoutRoundFinished) {
        this.timeoutRoundFinished = timeoutRoundFinished;
    }
}
