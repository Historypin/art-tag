package sk.eea.arttag.rest.api;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import sk.eea.arttag.model.LocalizedString;
import sk.eea.arttag.model.Tag;

public class TagDTO {
	private Long id;
	@NotNull
	@Pattern(regexp = "\\w{2,3}")
	private String language;
	@NotNull
	private String value;
	@NotNull
	private Long culturalObjectId;
	
	private String culturalObjectExternalId;
	private String culturalObjectDescription;
	private String culturalObjectExternalUrl;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public Long getCulturalObjectId() {
		return culturalObjectId;
	}
	public void setCulturalObjectId(Long culturalObjectId) {
		this.culturalObjectId = culturalObjectId;
	}
	public static TagDTO toDto(Tag tag) {
		TagDTO tagDto = new TagDTO();
		tagDto.setId(tag.getId());
		tagDto.setLanguage(tag.getValue().getLanguage());
		tagDto.setValue(tag.getValue().getValue());
		tagDto.setCulturalObjectId(tag.getCulturalObject().getId());
		tagDto.setCulturalObjectExternalId(tag.getCulturalObject().getExternalId());
		LocalizedString description = tag.getCulturalObject().getDescription().get(0);
        tagDto.setCulturalObjectDescription(description != null ? description.getValue():null);
        tagDto.setCulturalObjectExternalUrl(tag.getCulturalObject().getExternalUrl());
		return tagDto;
	}
    public String getCulturalObjectExternalId() {
        return culturalObjectExternalId;
    }
    public void setCulturalObjectExternalId(String culturalObjectExternalId) {
        this.culturalObjectExternalId = culturalObjectExternalId;
    }
    public String getCulturalObjectDescription() {
        return culturalObjectDescription;
    }
    public void setCulturalObjectDescription(String culturalObjectDescription) {
        this.culturalObjectDescription = culturalObjectDescription;
    }
    public String getCulturalObjectExternalUrl() {
        return culturalObjectExternalUrl;
    }
    public void setCulturalObjectExternalUrl(String culturalObjectExternalUrl) {
        this.culturalObjectExternalUrl = culturalObjectExternalUrl;
    }
}
