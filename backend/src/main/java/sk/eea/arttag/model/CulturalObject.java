package sk.eea.arttag.model;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * Entity storing data about cultural object.
 *
 * @author Maros Strmensky
 */
@Entity
@Table(name = "CULTURAL_OBJECT")
public class CulturalObject {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
    @SequenceGenerator(name = "hibernate_sequence", sequenceName = "hibernate_sequence")
    private Long id;
    private String author;
    private String externalId;
    private String externalUrl;
    private String externalSource;
    private String internalFileSystemPath;
    private String publicSource;
    private Long batchId;
    private Date lastSelected = new Date();
    private Integer numberOfSelections = 0;

    private Boolean active = Boolean.FALSE;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<LocalizedString> description;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "co_id")
    private List<Tag> tags;

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

    public String getInternalFileSystemPath() {
        return internalFileSystemPath;
    }

    public void setInternalFileSystemPath(String internalFileSystemPath) {
        this.internalFileSystemPath = internalFileSystemPath;
    }

    public String getPublicSource() {
        return publicSource;
    }

    public void setPublicSource(String publicSource) {
        this.publicSource = publicSource;
    }

    public Long getBatchId() {
        return batchId;
    }

    public void setBatchId(Long batchId) {
        this.batchId = batchId;
    }

    public Boolean isActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
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

    public Date getLastSelected() {
        return lastSelected;
    }

    public void setLastSelected(Date lastSelected) {
        this.lastSelected = lastSelected;
    }

    public Integer getNumberOfSelections() {
        return numberOfSelections;
    }

    public void setNumberOfSelections(Integer numberOfSelections) {
        this.numberOfSelections = numberOfSelections;
    }

    public String getDescriptionByLanguage(String lang, String defaultLang) {
        String description = null;
        Optional<LocalizedString> optional = getDescription().stream().filter(s -> lang.equalsIgnoreCase(s.getLanguage())).findAny();
        if (optional.isPresent()) {
            description = optional.get().getValue();
        } else {
            Optional<LocalizedString> optionalDef = getDescription().stream().filter(s -> defaultLang.equalsIgnoreCase(s.getLanguage())).findAny();
            if (optionalDef.isPresent()) {
                description = optional.get().getValue();
            } else {
                //failed to find description for either language or default language
            }
        }
        return description;
    }

}
