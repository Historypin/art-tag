package sk.eea.arttag.rest.api;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import sk.eea.arttag.model.CulturalObject;
import sk.eea.arttag.model.LocalizedString;

import javax.validation.constraints.NotNull;
import java.util.Map;
import java.util.stream.Collectors;

@ApiModel
public class CulturalObjectDTO {

    private Long id;
    private String author;
    @NotNull
    @ApiModelProperty("Identifier of external entity")
    private String externalId;
    private String externalUrl;
    @NotNull
    @ApiModelProperty("URL to object's downloadable image ")
    private String externalSource;
    @NotNull
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

    public String getExternalSource() {
        return externalSource;
    }

    public void setExternalSource(String externalSource) {
        this.externalSource = externalSource;
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
        result.setAuthor(culturalObjectDTO.getAuthor());
        if (culturalObjectDTO.getDescription() != null) {
            result.setDescription(
                    culturalObjectDTO.getDescription().entrySet().parallelStream().map(
                            desc -> new LocalizedString(desc.getKey(), desc.getValue())).collect(Collectors.toList())
            );
        }
        result.setExternalId(culturalObjectDTO.getExternalId());
        result.setExternalUrl(culturalObjectDTO.getExternalUrl());
        result.setExternalSource(culturalObjectDTO.getExternalSource());
        return result;
    }
}
