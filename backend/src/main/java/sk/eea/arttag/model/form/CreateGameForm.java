package sk.eea.arttag.model.form;

import org.hibernate.validator.constraints.NotEmpty;

public class CreateGameForm {

    @NotEmpty
    private String name;

    private boolean privateGame = false;

    private String joinGameLink;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isPrivateGame() {
        return privateGame;
    }

    public void setPrivateGame(boolean privateGame) {
        this.privateGame = privateGame;
    }

    public String getJoinGameLink() {
        return joinGameLink;
    }

    public void setJoinGameLink(String joinGameLink) {
        this.joinGameLink = joinGameLink;
    }

    @Override
    public String toString() {
        return "CreateGameForm{" +
                "name='" + name + '\'' +
                ", privateGame=" + privateGame +
                ", joinGameLink='" + joinGameLink + '\'' +
                '}';
    }
}


