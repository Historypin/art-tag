package sk.eea.arttag.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.*;

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

    private Boolean enabled;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.ALL)
    private Set<UserRole> userRole = new HashSet<UserRole>(0);

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

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Set<UserRole> getUserRole() {
        return userRole;
    }

    public void setUserRole(Set<UserRole> userRole) {
        this.userRole = userRole;
    }
}
