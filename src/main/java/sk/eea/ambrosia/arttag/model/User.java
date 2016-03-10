package sk.eea.ambrosia.arttag.model;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by silvia on 08/02/16.
 */


@XmlRootElement
public class User {

    private int id;

    private String name;

    private int score;

    public User() {
    }

    public User(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
