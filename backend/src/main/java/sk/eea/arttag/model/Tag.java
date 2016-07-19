package sk.eea.arttag.model;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name="TAG")
public class Tag {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
	@SequenceGenerator(name= "hibernate_sequence")
	private Long id;
	private Float hitScore;
	private Date created;

	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	private LocalizedString value;

	@ManyToOne
	@JoinColumn(name = "co_id")
	private CulturalObject culturalObject;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Float getHitScore() {
		return hitScore;
	}
	public void setHitScore(Float hitScore) {
		this.hitScore = hitScore;
	}
	public LocalizedString getValue() {
		return value;
	}
	public void setValue(LocalizedString value) {
		this.value = value;
	}
	public CulturalObject getCulturalObject() {
		return culturalObject;
	}
	public void setCulturalObject(CulturalObject culturalObject) {
		this.culturalObject = culturalObject;
	}
    public Date getCreated() {
        return created;
    }
    public void setCreated(Date created) {
        this.created = created;
    }
}
