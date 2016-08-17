package sk.eea.arttag.model;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "SYSTEM_USER")
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
    @SequenceGenerator(name= "hibernate_sequence")
    private Long id;

    @Column(name = "identity_provider_type")
    @Enumerated(EnumType.STRING)
    private IdentityProviderType identityProviderType;

    private String email;

    private String nickName;

    private String password;

    private Score personalScore;

    @ManyToMany
    @JoinTable(name = "USER_FAVOURITE")
    private List<CulturalObject> favouriteObjects;

    private Boolean enabled;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.ALL)
    private Set<UserRole> userRole = new HashSet<UserRole>(0);

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public IdentityProviderType getIdentityProviderType() {
        return identityProviderType;
    }

    public void setIdentityProviderType(IdentityProviderType identityProviderType) {
        this.identityProviderType = identityProviderType;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
