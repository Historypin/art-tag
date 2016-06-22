package sk.eea.arttag.rest.api;

import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import sk.eea.arttag.model.CulturalObject;
import sk.eea.arttag.model.LocalizedString;

@ApiModel
public class CulturalObjectDTO {

	private Long id;
	private String author;	
	@NotNull
	@ApiModelProperty("Identifier of external entity")
	private String externalId;
	private String externalUrl;
	@NotNull
	@ApiModelProperty("URL to image")
	private String imagePath;
	@NotNull
	@ApiModelProperty("Identifier of batch")
	private String batchId;
	private Map<String, String> description;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
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
	public String getBatchId() {
		return batchId;
	}
	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}
	public Map<String, String> getDescription() {
		return this.description;
	}
	public void setDescription(Map<String, String> description) {
		this.description = description;
	}
	
	public static CulturalObject toCulturalObject(CulturalObjectDTO culturalObjectDTO) {
		CulturalObject result = new CulturalObject();
		result.setId(culturalObjectDTO.getId());
		result.setBatchId(culturalObjectDTO.getBatchId());
		result.setAuthor(culturalObjectDTO.getAuthor());
		if(culturalObjectDTO.getDescription()!=null){
		result.setDescription(
				culturalObjectDTO.getDescription().entrySet().parallelStream().map(
						desc -> new LocalizedString(desc.getKey(), desc.getValue())).collect(Collectors.toList())
				);
		}
		result.setExternalId(culturalObjectDTO.getExternalId());
		result.setExternalUrl(culturalObjectDTO.getExternalUrl());
		result.setImagePath(culturalObjectDTO.getImagePath());
		return result;
	}	
}
