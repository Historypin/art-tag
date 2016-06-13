package sk.eea.arttag.model;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name="SYSTEM_USER")
public class User {

	@Id
	private String login;
	private String nickName;
	private String password;
	private Score personalScore;
	
	@ManyToMany
	@JoinTable(name="USER_FAVOURITE")
	private List<CulturalObject> favouriteObjects;

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Score getPersonalScore() {
		return personalScore;
	}

	public void setPersonalScore(Score personalScore) {
		this.personalScore = personalScore;
	}

	public List<CulturalObject> getFavouriteObjects() {
		return favouriteObjects;
	}

	public void setFavouriteObjects(List<CulturalObject> favouriteObjects) {
		this.favouriteObjects = favouriteObjects;
	}
}
