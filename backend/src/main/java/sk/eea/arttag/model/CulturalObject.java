package sk.eea.arttag.model;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * Entity storing data about cultural object.
 * @author Maros Strmensky
 */
@Entity
@Table(name="CULTURAL_OBJECT")
public class CulturalObject {

	@Id
	private long id;
	private String author;	
	private String externalId;
	private String externalUrl;
	private String imagePath;

	@OneToMany
	private List<LocalizedString> description;
	
	@OneToMany
	private List<Tag> tags;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	public String getExternalUrl() {
		return externalUrl;
	}

	public void setExternalUrl(String externalUrl) {
		this.externalUrl = externalUrl;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public List<LocalizedString> getDescription() {
		return description;
	}

	public void setDescription(List<LocalizedString> description) {
		this.description = description;
	}

	public List<Tag> getTags() {
		return tags;
	}

	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}
}
